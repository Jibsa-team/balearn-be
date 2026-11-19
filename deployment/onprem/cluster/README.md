# Balearn 앱 두 대 운영

운영 주소의 TCP 9443을 인프라 VM의 443으로 포워딩한다. 인프라 VM의 Caddy가 Blue와 Green의 FE·BE를 각각 분산한다. 앱 VM 한 대가 중단돼도 인프라와 다른 앱 VM이 작동하면 운영을 계속한다. 인프라 VM과 VMware 호스트는 공유 장애점이다.

## 노드 준비

| 역할 | 전용 LAN | VM 자원 |
| --- | --- | --- |
| 인프라 | 10.77.20.1/29 | 2 vCPU, 3 GiB, 60 GB |
| Blue | 10.77.20.2/29 | 2 vCPU, 2 GiB, 기존 디스크 |
| Green | 10.77.20.3/29 | 2 vCPU, 2 GiB, 30 GB |

세 주소는 Balearn 전용 VMware LAN Segment에서만 사용한다. 외부 인터페이스는 인프라가 브리지, Green이 NAT를 사용한다. 공유기에서 인프라의 브리지 주소를 DHCP 예약한다. 공급자 API와 GitHub 다운로드에 필요한 아웃바운드 연결을 유지한다. SSH 관리와 데이터 포트를 인터넷에 포워딩하지 않는다. 마이그레이션 중에는 현재 Blue와 인프라만 켜고, Blue가 작아진 뒤 Green을 켠다.

- 인프라의 구성 파일은 `/opt/balearn/cluster`에 root 소유로 설치한다. `infra.compose.yml`에서 MySQL·Redis·Elasticsearch 포트는 전용 인터페이스에만 바인딩된다. `vm.max_map_count=262144`를 저장하고 `nfsd.conf`를 NFS 설정에, `balearn.exports`를 `/etc/exports.d/balearn.exports`에 설치한다. NFS 및 RPC 포트는 외부 인터페이스에서 방화벽으로 차단한다.
- 앱 노드는 NFSv4 주소 `10.77.20.1:/srv/balearn/media`를 `/srv/balearn/media`에 마운트한다. `node-mount.conf`를 Docker systemd 의존성으로 설치하고, 파일에서 운영 백엔드의 숫자 UID/GID와 권한을 확인한다. 노드에서 Compose가 기존 디렉터리를 대신 만드는 동작을 차단한다.
- 인프라에 제어 전용 SSH 키를 생성한다. 두 노드의 `balearn` 계정에는 이 키를 `restrict,from="10.77.20.1",command="/opt/balearn/cluster/ssh-entry.sh"`로 제한한다. 스크립트와 Compose 파일은 root 소유로 설치한다. 인프라의 `known_hosts`는 각 노드 콘솔의 공개 호스트 키로 작성한다.
- 인프라 `/opt/balearn/shared/.env`에는 데이터 연결 설정과 `PRIVATE_IP`, `EDGE_LAN_IP`를 보관한다. TLS 인증서와 갱신 상태는 root 전용 경로로 가져오고, Caddy에는 `/opt/balearn/shared/tls`만 읽기 전용 마운트한다. 두 노드의 `node.env`에는 동일한 운영 DB·Redis·JWT·카카오 설정과 각각의 `PRIVATE_IP`를 권한 0600으로 보관한다.

## 최초 데이터 이전

1. 이전 백업 복원 연습과 용량 검증을 마친 뒤 점검 시간을 잡는다. 단일 노드 `balearn-cd.timer`를 중지한다. 사용자 요청을 잠시 점검 응답으로 전환하고 기존 백엔드의 쓰기와 채팅 배치를 종료한 다음 MySQL·Redis·Elasticsearch를 정상 중지한다.
2. Blue에서 root로 `volumes.sh capture /opt/balearn/backups/<이전식별자>`를 실행한다. 생성된 4개 아카이브와 체크섬을 내부 SSH로만 전송한다. 인프라에서 `volumes.sh restore`를 같은 인수로 실행한다. 대상에 기존 컨테이너 또는 데이터가 있는 경우 별도 새 대상에서 복원한다. 원본 볼륨은 보존한다.
3. 복원한 DB·Redis·Elasticsearch를 시작하고 기존 스키마, 파일 샘플 체크섬과 권한을 검사한다. 두 앱 노드에서 NFS 마운트와 운영 DB 연결을 확인한다. `rolling-cd.py --prepare-only`로 GitHub의 FE·BE 이미지와 정적 파일을 배치한 뒤 두 노드에 출력된 SHA 쌍을 `node-agent.py "activate <BE SHA> <FE SHA>"`로 적용한다. 초기 배포 이미지부터 클러스터 세션과 새 채팅 배치가 들어 있어야 한다.
4. LAN에서 공인 TLS·카카오 목적지·액추에이터 차단을 먼저 검증한다. 공유기의 Balearn 규칙을 인프라로 변경하고 외부망에서 접속을 검증한다. 새 데이터 저장소에서 쓰기를 허용하기 전에는 포워딩과 기존 앱을 복구해 이전을 중단할 수 있다. 쓰기 재개 이후에는 이전 앱 버전을 새 저장소에 배포하는 방식으로 복구한다.

## 롤링 배포

인프라의 `/opt/balearn/cd/deploy-images.py`에는 기존 GitHub 아카이브 검사·로더를 설치한다. `edge-address.env`에 `BALEARN_EDGE_IP`를 설정하고 `balearn-rolling.timer`를 활성화한다. 한 번에 하나의 배포만 실행한다. 두 노드와 외부 Caddy 검증을 통과해야 다음 노드를 교체하며, 장애 시 배포 일지에 남긴 이전 조합으로 복구한다. 타이머·상태 확인은 `systemctl list-timers balearn-rolling.timer`와 `journalctl -u balearn-rolling.service`를 사용한다.

운영 전에 두 앱 VM의 순차 중단, 다른 노드에서의 OAuth 콜백, 웹소켓과 SockJS 재접속, 파일 권한, 새 릴리스 기동 실패와 이전 릴리스 복귀를 점검한다. DB와 API 변경은 이전 버전과 함께 실행 가능한 형태로 반영한다.
