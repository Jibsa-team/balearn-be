#!/usr/bin/env python3
"""Restrict the infrastructure VM to staging and activating Balearn images."""

import json
import os
from pathlib import Path
import re
import subprocess
import sys

ROOT = Path("/opt/balearn/cluster")
COMPONENTS = ("backend", "frontend")
SHA = re.compile(r"[a-f0-9]{40}")


def run(*args, **kwargs):
    return subprocess.run(args, check=True, timeout=120, **kwargs)


def image(component, sha):
    return f"balearn-{component}:{sha}"


def status():
    versions = {}
    for component in COMPONENTS:
        try:
            result = run("docker", "inspect", "--format", "{{.Config.Image}}",
                         f"balearn-node-{component}-1", capture_output=True, text=True)
        except subprocess.CalledProcessError as error:
            if "No such object" not in error.stderr:
                raise
            versions[component] = None
            continue
        match = re.fullmatch(fr"balearn-{component}:([a-f0-9]{{40}})", result.stdout.strip())
        if not match:
            raise ValueError(f"Unknown {component} deployment")
        versions[component] = match.group(1)
    print(json.dumps(versions), flush=True)


def activate(shas):
    releases = dict(zip(COMPONENTS, shas, strict=True))
    for component, sha in releases.items():
        run("docker", "image", "inspect", image(component, sha), stdout=subprocess.DEVNULL)
    run("mountpoint", "-q", "/srv/balearn/media")
    values = {f"BALEARN_{component.upper()}_IMAGE": image(component, sha)
              for component, sha in releases.items()}
    run("docker", "compose", "--env-file", str(ROOT / "node.env"),
        "-f", str(ROOT / "node.compose.yml"), "up", "-d", "--no-build",
        "--pull", "never", env=os.environ | values)
    temp = ROOT / ".node.images.tmp"
    temp.write_text("".join(f"{key}={value}\n" for key, value in values.items()))
    temp.replace(ROOT / "node.images")


def main():
    if len(sys.argv) != 2:
        raise ValueError("Expected one deployment command")
    words = sys.argv[1].split()
    if words == ["status"]:
        status()
    elif len(words) == 3 and words[0] == "stage" and words[1] in COMPONENTS and SHA.fullmatch(words[2]):
        run("docker", "load", stdin=sys.stdin.buffer)
        run("docker", "image", "inspect", image(words[1], words[2]), stdout=subprocess.DEVNULL)
    elif len(words) == 3 and words[0] == "activate" and all(SHA.fullmatch(word) for word in words[1:]):
        activate(words[1:])
    else:
        raise ValueError("Unknown deployment command")


if __name__ == "__main__":
    main()
