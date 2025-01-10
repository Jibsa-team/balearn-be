#!/bin/bash

update_nginx_config() {
    local COLOR=$1
    local NGINX_CONF_DIR="/home/${{ secrets.USERNAME }}/app/nginx"
    local TEMPLATE="${NGINX_CONF_DIR}/default.conf.template"
    local TARGET_CONF="/etc/nginx/conf.d/default.conf"

    # nginx 컨테이너 내부에 새 설정 생성
    docker exec nginx sh -c "export COLOR=${COLOR} && envsubst '\${COLOR}' < ${TEMPLATE} > ${TARGET_CONF}"

    # nginx 설정 테스트
    if docker exec nginx nginx -t; then
        # nginx 재시작
        docker exec nginx nginx -s reload
        echo "Nginx configuration updated to route to ${COLOR} deployment"
        return 0
    else
        echo "Nginx configuration test failed"
        return 1
    fi
}