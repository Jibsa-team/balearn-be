#!/usr/bin/env python3
"""Deploy to healthy Balearn nodes one at a time."""

import argparse
import fcntl
import json
import os
from pathlib import Path
import runpy
import socket
import subprocess
import tempfile
import time
from urllib.parse import urlsplit

ROOT = Path("/opt/balearn/cluster")
SHARED = Path("/opt/balearn/shared")
STATE = Path("/opt/balearn/cd/state")
NODES = {"green": "10.77.20.3", "blue": "10.77.20.2"}
REPOS = {"backend": "balearn-be", "frontend": "balearn-fe"}
HOST = "hmwg.balearn.o-r.kr"
ORIGIN = f"https://{HOST}:9443"
EDGE = ["docker", "compose", "--env-file", str(SHARED / ".env"), "-f", str(ROOT / "infra.compose.yml")]
UPSTREAMS = ROOT / "edge" / "upstreams.caddy"
JOURNAL = STATE / "rolling.json"
FAILED = STATE / "rolling-failed.json"


def run(*args, **kwargs):
    return subprocess.run(args, check=True, timeout=120, **kwargs)


def atomic(path, contents):
    tmp = path.with_name(f".{path.name}.tmp")
    tmp.write_text(contents, encoding="utf-8")
    tmp.replace(path)


def ssh(node, *words, **kwargs):
    return run("ssh", "-i", str(ROOT / "deploy_ed25519"),
               "-o", f"UserKnownHostsFile={ROOT / 'known_hosts'}",
               "-o", "StrictHostKeyChecking=yes", "-o", "BatchMode=yes",
               "-o", "ConnectTimeout=6", f"balearn@{NODES[node]}", *words, **kwargs)


def status(node):
    return json.loads(ssh(node, "status", capture_output=True, text=True).stdout)


def route(nodes):
    if not nodes:
        raise ValueError("At least one node is required")
    previous = UPSTREAMS.read_text()
    new = "".join(f"(app_{name}) {{\n    to " +
                  " ".join(f"{NODES[node]}:{port}" for node in nodes) + "\n}\n"
                  for name, port in (("backends", 8080), ("frontends", 3000)))
    atomic(UPSTREAMS, new)
    try:
        run(*EDGE, "exec", "-T", "edge", "caddy", "reload",
            "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile",
            "--address", "unix//tmp/balearn-caddy-admin.sock")
    except Exception:
        atomic(UPSTREAMS, previous)
        raise


def curl(url, *flags):
    return run("curl", "-fsS", "--max-time", "4", *flags, url, capture_output=True).stdout


def verify(node):
    addr = NODES[node]
    for _ in range(24):
        try:
            html = curl(f"http://{addr}:3000/")
            health = json.loads(curl(f"http://{addr}:8080/actuator/health"))
            sockjs = json.loads(curl(f"http://{addr}:8080/ws/info",
                                     "-H", f"Origin: {ORIGIN}"))
            redirect = curl(f"http://{addr}:8080/oauth2/authorization/kakao", "-o", "/dev/null",
                            "-w", "%{redirect_url}")
            if ("카카오 로그인".encode() in html and health["status"] == "UP"
                    and sockjs["websocket"] and urlsplit(redirect.decode()).hostname == "kauth.kakao.com"):
                return
        except (subprocess.SubprocessError, KeyError, ValueError):
            pass
        time.sleep(2)
    raise RuntimeError(f"{node} did not become ready")


def stage_images(versions, assets, release_tools):
    for component, sha in versions.items():
        image = f"balearn-{component}:{sha}"
        if subprocess.run(["docker", "image", "inspect", image], stdout=subprocess.DEVNULL,
                          stderr=subprocess.DEVNULL).returncode:
            release_tools["load_image"](assets[component], image)
        with tempfile.TemporaryFile(dir=STATE) as archive:
            run("docker", "save", image, stdout=archive)
            for node in NODES:
                if status(node)[component] != sha:
                    archive.seek(0)
                    ssh(node, "stage", component, sha, stdin=archive)
    serve_static(versions["frontend"])


def serve_static(sha):
    container = run("docker", "create", f"balearn-frontend:{sha}", capture_output=True, text=True).stdout.strip()
    try:
        with tempfile.TemporaryDirectory(dir=STATE) as temp:
            run("docker", "cp", f"{container}:/app/.next/static/.", temp)
            directory = Path(temp)
            for asset in directory.rglob("*"):
                if asset.is_symlink():
                    raise ValueError("Unexpected static asset symlink")
                if asset.is_file():
                    target = SHARED / "static" / asset.relative_to(directory)
                    target.parent.mkdir(mode=0o755, parents=True, exist_ok=True)
                    data = asset.read_bytes()
                    if target.exists() and target.read_bytes() != data:
                        raise ValueError(f"Static asset collision: {asset.name}")
                    if not target.exists():
                        candidate = target.with_name(f".{target.name}.pending")
                        candidate.write_bytes(data)
                        candidate.chmod(0o644)
                        candidate.replace(target)
    finally:
        run("docker", "rm", container, stdout=subprocess.DEVNULL)


def replace_node(node, versions):
    peer = next(name for name in NODES if name != node)
    verify(peer)
    route([peer])
    time.sleep(35)
    ssh(node, "activate", versions["backend"], versions["frontend"])
    verify(node)
    route(NODES)
    edge_ip = os.environ["BALEARN_EDGE_IP"]
    for _ in range(3):
        html = curl(f"{ORIGIN}/", "--connect-to", f"{HOST}:9443:{edge_ip}:443")
        if "카카오 로그인".encode() not in html:
            raise RuntimeError("Public home check failed")
        time.sleep(2)
    print(f"{node}: {versions}", flush=True)


def recover(previous):
    nodes = previous["nodes"]
    first = previous["replacing"]
    for node in (first, *(node for node in nodes if node != first)):
        try:
            if status(node) == nodes[node]:
                verify(node)
                continue
        except (RuntimeError, subprocess.SubprocessError):
            pass
        replace_node(node, nodes[node])
    route(NODES)
    atomic(FAILED, json.dumps(previous["candidate"]))
    JOURNAL.unlink()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--prepare-only", action="store_true", help="stage images without changing traffic")
    args = parser.parse_args()
    if socket.gethostname().split(".")[0] != "balearn-infra":
        raise RuntimeError("Rollout belongs on the Balearn infrastructure VM")
    STATE.mkdir(mode=0o700, parents=True, exist_ok=True)
    with (STATE / "rolling.lock").open("w") as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        if JOURNAL.exists():
            recover(json.loads(JOURNAL.read_text()))
            return
        release_tools = runpy.run_path(str(ROOT.parent / "cd" / "deploy-images.py"))
        results = {component: release_tools["latest_image"](repo) for component, repo in REPOS.items()}
        candidate = {component: result[0] for component, result in results.items()}
        assets = {component: result[1] for component, result in results.items()}
        if args.prepare_only:
            if not all(assets.values()):
                raise RuntimeError("Both production images must be ready")
            stage_images(candidate, assets, release_tools)
            print(json.dumps(candidate), flush=True)
            return
        previous = {node: status(node) for node in NODES}
        if any(not all(versions.values()) for versions in previous.values()):
            raise RuntimeError("Initialize both app nodes before enabling automatic rollout")
        if all(versions == candidate for versions in previous.values()):
            return
        if FAILED.exists() and json.loads(FAILED.read_text()) == candidate:
            return
        if not all(assets.values()):
            return
        for node in NODES:
            verify(node)
        stage_images(candidate, assets, release_tools)
        try:
            for node in NODES:
                if previous[node] != candidate:
                    transaction = {"nodes": previous, "candidate": candidate, "replacing": node}
                    atomic(JOURNAL, json.dumps(transaction))
                    replace_node(node, candidate)
        except Exception:
            if JOURNAL.exists():
                recover(json.loads(JOURNAL.read_text()))
            raise
        JOURNAL.unlink()


if __name__ == "__main__":
    main()
