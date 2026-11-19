#!/usr/bin/env bash
set -euo pipefail
umask 077

action=${1:?capture or restore}
backup=${2:?use an absolute backup directory}
[[ "$backup" =~ ^/opt/balearn/backups/[a-zA-Z0-9._-]+$ ]]
[[ ${backup##*/} != . && ${backup##*/} != .. ]]
[[ $(id -u) == 0 ]]
components=(mysql redis elasticsearch media)

case "$action" in
  capture)
    [[ $(hostname -s) == balearn-onprem ]]
    for component in backend frontend mysql redis elasticsearch; do
      name="balearn-onprem-$component-1"
      [[ $(docker inspect --format '{{.State.Running}}' "$name") == false ]] || {
        printf '%s must be stopped before capture\n' "$name" >&2
        exit 1
      }
    done
    mkdir -m 700 "$backup"
    for component in "${components[@]}"; do
      volume=$(docker volume inspect --format '{{.Mountpoint}}' "balearn-onprem_$component")
      tar --numeric-owner -C "$volume" -czf "$backup/$component.tar.gz" .
    done
    cd "$backup"
    sha256sum mysql.tar.gz redis.tar.gz elasticsearch.tar.gz media.tar.gz > SHA256SUMS
    ;;
  restore)
    [[ $(hostname -s) == balearn-infra ]]
    cd "$backup"
    sha256sum --strict -c SHA256SUMS
    for component in mysql redis elasticsearch; do
      name="balearn-infra_$component"
      [[ -z $(docker ps -a --filter "volume=$name" -q) ]] || {
        printf 'Remove the stopped target container before restoring %s\n' "$name" >&2
        exit 1
      }
      docker volume create "$name" >/dev/null
      volume=$(docker volume inspect --format '{{.Mountpoint}}' "$name")
      [[ -z $(find "$volume" -mindepth 1 -print -quit) ]]
    done
    install -d -m 755 /srv/balearn/media
    [[ -z $(find /srv/balearn/media -mindepth 1 -print -quit) ]]
    for component in mysql redis elasticsearch; do
      volume=$(docker volume inspect --format '{{.Mountpoint}}' "balearn-infra_$component")
      tar --numeric-owner -C "$volume" -xzf "$component.tar.gz"
    done
    tar --numeric-owner -C /srv/balearn/media -xzf media.tar.gz
    ;;
  *)
    printf 'Expected capture or restore\n' >&2
    exit 2
    ;;
esac
