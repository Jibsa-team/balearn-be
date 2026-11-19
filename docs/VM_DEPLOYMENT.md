# 새 VMware VM에서 Balearn 운영

앱 VM 두 대의 동시 운영과 롤링 배포 절차는 [다중 VM 운영 가이드](../deployment/onprem/cluster/README.md)에 정리한다.

이 배포는 VMware VM `balearn-onprem`과 TCP `9443`을 사용한다. VM은 Ubuntu Server 24.04 LTS, 2 vCPU, RAM 4GB, 60GB 가상 디스크로 구성한다. 기존 VMware 서비스의 포워딩과 VM 설정은 건드리지 않는다.

| 항목 | 구성 |
|---|---|
| 공개 주소 | `https://hmwg.balearn.o-r.kr:9443` |
| 프록시 | 새 VM의 Caddy. 하나의 HTTPS 주소에서 웹, API, WebSocket을 경로로 분기 |
| 인증 | 공개 운영은 카카오 로그인. LAN 검증은 Keycloak의 `balearn` 영역을 사용 |
| 자료 | 로컬 영구 볼륨. 학습 자료는 인증된 모임 멤버만 다운로드. 공개 미디어는 프로필 이미지용 |
| 배포 단위 | 앱 이미지와 Compose 설정을 `/opt/balearn/releases/<버전>`에 보관. 운영 값은 `/opt/balearn/shared/.env`에 보관 |

## 현재 배포 기록

- VM의 LAN 주소는 공유기의 DHCP 예약과 VM의 환경 파일에서 확인한다. 운영 모드는 `/opt/balearn/shared/active-release`에서 확인한다.
- Windows에서 LAN 인증서를 검증해 웹이 HTTP 200을 반환하는 것을 확인했다. VM에서는 OIDC 로그인과 토큰 발급, 자료 업로드·멤버 다운로드·비회원 거부, 삭제 후 404, 로그아웃, 관리 경로 차단과 SockJS 진입점을 검증했다.
- 공개 직전 데이터 백업의 체크섬을 `/opt/balearn/backups/pre-public-20261118T060702Z`에서 검증했고 VMware에 `balearn-pre-public` 스냅샷을 만들었다. VM을 다시 켠 뒤 백엔드 상태는 `UP`이었다.
- 현재 공인 인증서는 Let’s Encrypt 수동 DNS-01 방식으로 발급했다. 인증서 만료 전에 새 TXT 값으로 인증하고 재배포한다.

## LAN 배포

새 VM에 Docker Engine과 Compose를 설치한다. Elasticsearch를 위해 `vm.max_map_count=262144`를 설정한다. SSH는 키 인증을 사용하고 공유기에서 포워딩하지 않는다.

두 저장소의 소스 및 잠금 파일과 상위 폴더의 Compose 파일, `deployment/onprem`을 릴리스 경로에 복사한다. 앱 이미지는 그 버전의 이름으로 순차 빌드한다. 새 VM 안에서 아래를 실행한다.

```bash
sudo bash /opt/balearn/releases/<버전>/deployment/onprem/deploy-vm.sh lan <VM-LAN-IP>
```

LAN 모드는 Caddy의 로컬 인증 기관으로 HTTPS를 제공한다. 클라이언트에서 파일 `/opt/balearn/shared/trust/caddy-local.crt`의 지문을 확인한 다음 신뢰하거나, 해당 인증서를 검증에만 사용한다. Windows에서 이 도메인이 LAN 주소로 조회되지 않으면 브라우저 검증 동안 호스트 이름을 VM IP에 매핑한다. CLI에서 인증서 검증을 유지한 채 접속하려면 아래와 같이 실행한다.

```bash
curl --cacert caddy-local.crt --connect-to hmwg.balearn.o-r.kr:9443:<VM-LAN-IP>:443 \
  https://hmwg.balearn.o-r.kr:9443/
```

## 공개 전환

1. 공유기에서 VM의 고정 DHCP 예약을 확인한다.
2. 카카오 앱의 REST API 키에 `https://hmwg.balearn.o-r.kr:9443/login/oauth2/code/kakao`를 리디렉션 URI로 등록한다. VM에서 `sudoedit /opt/balearn/shared/.env`로 `KAKAO_CLIENT_ID`와 `KAKAO_CLIENT_SECRET`을 설정한다.
3. Certbot의 `/etc/letsencrypt`을 VM의 `/opt/balearn/shared/letsencrypt`에 연결하고 수동 DNS-01로 공인 인증서를 발급한 뒤 `deploy-vm.sh public-cert <VM-LAN-IP>`를 실행한다. DuckDNS DNS-01 자동 발급을 쓰는 인스턴스는 `BALEARN_ACME_DUCKDNS`, `DUCKDNS_API_TOKEN` 및 DNS 검증용 CNAME을 설정하고 `public`으로 배포한다. 공개 모드는 카카오 버튼을 빌드하고 Keycloak 경로를 차단한다.
4. 공유기에서 외부 TCP 9443을 새 VM의 TCP 443에 전달한다. 모바일 데이터망에서 로그인, 자료 다운로드와 채팅을 확인한다. WAN 주소가 공인 접속을 받을 수 없으면 LAN 검증 상태로 유지한다.

## 운영 확인과 복구

- 각 앱 저장소의 `develop-2026`을 대상으로 하는 PR에서 CI가 실행된다. 머지로 이 브랜치에 커밋이 들어오면 CI가 성공한 이미지가 `production-<커밋 SHA>` GitHub Release에 게시된다. 브랜치에 직접 푸시할 수 있는 권한은 팀의 브랜치 규칙으로 제한한다.
- 배포 도구와 systemd 유닛은 백엔드 저장소의 [deployment/onprem](../deployment/onprem)에 있다. 운영 VM의 `/opt/balearn/cd/deploy-images.py`를 `balearn-cd.timer`가 5분마다 실행한다. 이미지 수신에 쓰는 연결은 VM에서 GitHub로 나간다. GitHub에는 VM 접속 키와 앱 비밀값을 등록할 필요가 없다.
- 배포 도구는 현재 브랜치 커밋용 이미지의 SHA-256을 검증하고 앱 컨테이너만 교체한다. 헬스 체크 실패 시 이전 이미지로 돌아간다. DB 스키마는 앱 배포 도구가 변경하지 않는다. 운영 반영 상태는 `sudo journalctl -u balearn-cd.service`와 `sudo docker ps`에서 확인한다.
- 운영 VM에서는 프런트엔드 컴파일로 서비스가 지연될 수 있다. 별도 환경에서 공개 API 주소와 `NEXT_PUBLIC_LOGIN_PROVIDERS=kakao`로 빌드하고, 이미지들을 VM에 로드한 뒤 `deploy-vm.sh public-cert <VM-LAN-IP> --no-build`로 배포한다.
- `/opt/balearn/shared/active-release`의 버전과 공개 모드를 확인한다. 앱 상태는 VM 안의 `http://127.0.0.1:8080/actuator/health`로 확인한다.
- 첫 공개 전에 새 VM의 복구 지점을 만들고 Compose 볼륨(MySQL, Redis, Elasticsearch, 미디어, Keycloak DB, Caddy)과 `shared` 설정을 백업한다. 파일 복사 방식으로 볼륨을 백업할 때는 새 VM의 스택을 정상 종료한 상태에서 수행한다. 백업에 DB와 운영 비밀이 있으므로 접근을 제한한다.
- 외부 전환에 실패하면 새 9443 규칙만 비활성화하고, 마지막으로 확인한 릴리스의 `deploy-vm.sh lan`을 다시 실행한다. 스키마와 함께 돌려야 하는 경우에는 그 릴리스에 맞는 백업을 복원한다.
- GCS 연결 시 자료용 버킷은 공개 미디어 버킷과 분리하고 `GCS_LEARNING_BUCKET`에 지정한다. 이 버킷에서는 공개 접근을 허용하지 않는다. 기존 공개 버킷에 있던 자료는 파일과 DB 참조를 함께 이전한다.
