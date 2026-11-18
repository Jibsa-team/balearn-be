#!/usr/bin/env python3
"""Update Balearn application containers from tested GitHub releases."""

import argparse
import fcntl
import hashlib
import ipaddress
import json
import os
from pathlib import Path
import re
import shutil
import socket
import subprocess
import tempfile
import time
from urllib.error import HTTPError
from urllib.request import Request, urlopen


ROOT = Path("/opt/balearn")
SHARED = ROOT / "shared"
STATE = ROOT / "cd" / "state"
BRANCH = "develop-2026"
REPOS = {"backend": "balearn-be", "frontend": "balearn-fe"}
SHA = r"[a-f0-9]{40}"
ASSET = "image.tar.gz"


def github_json(path):
    request = Request(f"https://api.github.com/{path}", headers={
        "Accept": "application/vnd.github+json", "User-Agent": "balearn-onprem-cd",
    })
    with urlopen(request, timeout=15) as response:
        return json.load(response)


def latest_image(repo):
    repository = f"Jibsa-team/{repo}"
    prefix = f"repos/{repository}"
    sha = github_json(f"{prefix}/branches/{BRANCH}")["commit"]["sha"]
    if not re.fullmatch(SHA, sha):
        raise ValueError("Invalid GitHub commit SHA")
    tag = f"production-{sha}"
    try:
        release = github_json(f"{prefix}/releases/tags/{tag}")
    except HTTPError as error:
        if error.code == 404:
            return sha, None
        raise
    if release["tag_name"] != tag or release["draft"]:
        raise ValueError("Unexpected release metadata")
    for asset in release["assets"]:
        url = f"https://github.com/{repository}/releases/download/{tag}/{ASSET}"
        if (
            asset["name"] == ASSET
            and asset["state"] == "uploaded"
            and asset["browser_download_url"] == url
            and re.fullmatch(r"sha256:[a-f0-9]{64}", asset.get("digest") or "")
            and 0 < asset["size"] < 1024**3
        ):
            return sha, asset
    raise ValueError(f"{repo}: release asset or digest is missing")


def docker(*args):
    return subprocess.check_output(["docker", *args], text=True, timeout=120).strip()


def update_container(release, component, image):
    env = os.environ | {
        "BALEARN_BACKEND_IMAGE": f"balearn-backend:{release.name}",
        "BALEARN_FRONTEND_IMAGE": f"balearn-frontend:{release.name}",
        "LOGIN_PROVIDERS": "kakao", "SPRING_PROFILES_ACTIVE": "dev",
        "JPA_DDL_AUTO": "validate",
    }
    env[f"BALEARN_{component.upper()}_IMAGE"] = image
    subprocess.run([
        "docker", "compose", "--env-file", str(SHARED / ".env"), "-p", "balearn-onprem",
        "-f", "compose.balearn-local.yml", "-f", "compose.balearn-onprem.yml",
        "-f", "deployment/onprem/compose.vm.yml",
        "up", "-d", "--no-deps", "--no-build", "--pull", "never", component,
    ], cwd=release, env=env, check=True, timeout=90)


def fetch(url, *args):
    return subprocess.check_output(
        ["curl", "-fsS", "--max-time", "5", *args, url], timeout=8,
    )


def healthy(component, host, lan_ip):
    origin = f"https://{host}:9443"
    route = f"{host}:9443:{lan_ip}:443"
    for _ in range(24):
        try:
            if component == "backend":
                status = json.loads(fetch("http://127.0.0.1:8080/actuator/health"))
                headers = fetch(f"{origin}/oauth2/authorization/kakao", "--connect-to", route,
                                "-D", "-", "-o", "/dev/null")
                ok = status["status"] == "UP" and b"location: https://kauth.kakao.com/" in headers.lower()
            else:
                html = fetch(f"{origin}/", "--connect-to", route)
                ok = "카카오 로그인".encode() in html and "조직 계정으로 로그인".encode() not in html
            if ok:
                return
        except (subprocess.SubprocessError, ValueError, KeyError):
            pass
        time.sleep(3)
    raise RuntimeError(f"{component}: health check failed")


def load_image(asset, image):
    digest = hashlib.sha256()
    received = 0
    if shutil.disk_usage(ROOT).free < 3 * 1024**3:
        raise RuntimeError("Less than 3 GiB free for an image update")
    with tempfile.TemporaryFile(dir=STATE) as archive:
        request = Request(asset["browser_download_url"], headers={"User-Agent": "balearn-onprem-cd"})
        deadline = time.monotonic() + 120
        with urlopen(request, timeout=20) as response:
            while chunk := response.read(1024 * 1024):
                received += len(chunk)
                if received > asset["size"] or time.monotonic() > deadline:
                    raise ValueError("Unexpected release download size or duration")
                digest.update(chunk)
                archive.write(chunk)
        if received != asset["size"] or f"sha256:{digest.hexdigest()}" != asset["digest"]:
            raise ValueError("Release image checksum mismatch")
        archive.seek(0)
        subprocess.run(["docker", "load"], stdin=archive, timeout=120, check=True)
    docker("image", "inspect", image)


def deploy(release, component, repo, host, lan_ip, check_only):
    rollback = f"balearn-{component}:cd-rollback"
    pending = STATE / f"{component}.pending"
    failed = STATE / f"{component}.failed"
    if pending.exists() and not check_only:
        update_container(release, component, rollback)
        healthy(component, host, lan_ip)
        os.replace(pending, failed)

    sha, asset = latest_image(repo)
    image = f"balearn-{component}:{sha}"
    if check_only:
        healthy(component, host, lan_ip)
        print(f"{component}: healthy, branch {sha[:12]}, release {'ready' if asset else 'pending'}")
        return
    if asset is None or (failed.exists() and failed.read_text().strip() == sha):
        return

    container = f"balearn-onprem-{component}-1"
    current_image = docker("inspect", "--format", "{{.Image}}", container)
    if docker("inspect", "--format", "{{.Config.Image}}", container) == image:
        return
    load_image(asset, image)
    docker("image", "tag", current_image, rollback)
    pending.write_text(sha + "\n")
    try:
        update_container(release, component, image)
        healthy(component, host, lan_ip)
    except Exception:
        update_container(release, component, rollback)
        healthy(component, host, lan_ip)
        os.replace(pending, failed)
        docker("image", "rm", image)
        raise
    pending.unlink()
    if failed.exists():
        failed.unlink()
    print(f"{component}: deployed {sha}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true", help="check production and release availability")
    args = parser.parse_args()
    if socket.gethostname().split(".")[0] != "balearn-onprem":
        raise RuntimeError("This deployer requires the dedicated Balearn VM")
    release_name, mode = (SHARED / "active-release").read_text().splitlines()
    if mode != "public-cert" or not re.fullmatch(r"release-[a-zA-Z0-9_.-]+", release_name):
        raise RuntimeError("Balearn production Compose release is inactive")
    release = ROOT / "releases" / release_name
    config = dict(re.findall(r"^(BALEARN_PUBLIC_HOST|BALEARN_LAN_IP)=([^\r\n]+)",
                             (SHARED / ".env").read_text(), re.MULTILINE))
    host = config["BALEARN_PUBLIC_HOST"]
    if not re.fullmatch(r"[a-zA-Z0-9.-]+", host):
        raise ValueError("Invalid Balearn host")
    lan_ip = str(ipaddress.IPv4Address(config["BALEARN_LAN_IP"]))
    STATE.mkdir(mode=0o700, parents=True, exist_ok=True)
    with (STATE / "lock").open("w") as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        for component, repo in REPOS.items():
            deploy(release, component, repo, host, lan_ip, args.check)


if __name__ == "__main__":
    main()
