#!/bin/bash

# Variables
NGINX_CONTAINER_NAME="nginx-proxy"
ACTIVE_ENV=$1  # "blue" or "green"

# Check if environment argument is provided
if [[ -z "$ACTIVE_ENV" ]]; then
  echo "Usage: ./update_nginx.sh <blue|green>"
  exit 1
fi

# Set Nginx configuration based on active environment
if [[ "$ACTIVE_ENV" == "blue" ]]; then
  CONFIG="upstream app { server app-blue:8080; }"
elif [[ "$ACTIVE_ENV" == "green" ]]; then
  CONFIG="upstream app { server app-green:8080; }"
else
  echo "Invalid environment: $ACTIVE_ENV. Use 'blue' or 'green'."
  exit 1
fi

# Update the Nginx configuration
docker exec $NGINX_CONTAINER_NAME bash -c "echo \"$CONFIG\" > /etc/nginx/conf.d/default.conf"

# Reload Nginx to apply changes
docker exec $NGINX_CONTAINER_NAME nginx -s reload

echo "Nginx updated to route traffic to $ACTIVE_ENV environment."
