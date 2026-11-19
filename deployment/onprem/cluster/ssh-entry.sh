#!/bin/sh
set -eu
exec /usr/bin/sudo -n /usr/bin/python3 /opt/balearn/cluster/node-agent.py "${SSH_ORIGINAL_COMMAND:?Missing command}"
