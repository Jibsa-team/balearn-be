# Balearn 배포와 외부 서비스 설정

두 저장소를 `balearn-be`, `balearn-fe` 디렉터리에 나란히 두고, 상위 작업 폴더에서 Compose 명령을 실행한다.

| 영역 | 기존 구성 | 로컬·온프레미스 구성 | 클라우드 연결점 |
|---|---|---|---|
| 파일과 프로필 이미지 | GCS 버킷·서비스 계정·CDN | `/data/media` 영구 볼륨. 백엔드 `/media`에서 내려받기. 기본 이미지는 FE에 포함 | `MEDIA_STORAGE_PROVIDER=gcs`, `BUCKET_NAME`, `GCS_PUBLIC_URL`, ADC 또는 `GCS_KEY` |
| 인증 | Kakao·Google·GitHub OAuth | Keycloak 또는 조직 OIDC. Balearn 세션은 기존 JWT·Redis 방식 사용 | 동일 OIDC 등록 또는 기존 소셜 제공자 설정 |
| 데이터 | 외부 MySQL, Redis, Elasticsearch 주소. 실제 제공자와 리소스 ID는 저장소에 없음 | Docker 서비스 또는 사내 서비스 주소 | MySQL JDBC URL, Redis 호스트·포트·ACL·TLS, Elasticsearch URL·Basic 인증 |
| HTTP와 채팅 | 하드코딩된 Balearn·Vercel 도메인과 Nginx | API, 웹 URL, REST·SockJS 허용 출처를 환경 변수로 지정 | 도메인과 TLS 프록시 설정 변경 |
| 프런트엔드 | Vercel 주소와 Google Fonts 빌드 요청 | Next.js standalone 컨테이너와 저장소에 포함된 글꼴 | 동일 이미지 빌드 방식으로 VM·컨테이너 서비스에 배포 |
| 백엔드 배포 | GitHub Actions에서 `asia.gcr.io`로 이미지 전송 후 SSH·Nginx 블루/그린 전환 | 로컬 이미지 빌드와 Compose에서 단일 백엔드 실행 | 임의 OCI 레지스트리의 이미지 태그로 실행. GCR 워크플로는 과거 배포 경로 |
| 운영 도구 | 선택형 Kibana·Logstash Compose 정의 | 기본 실행은 MySQL, Redis, Elasticsearch와 Actuator 사용 | 필요 시 기존 운영 도구나 사내 관측 시스템 연동 |

## 로컬에서 클라우드 계정 없이 실행

PowerShell에서 처음 한 번 Keycloak 관리자 비밀번호와 Balearn OIDC 클라이언트 시크릿을 정한다. 이후 재기동에도 같은 값을 사용한다. Compose로 읽는 개인 환경 파일에 보관할 경우 경로를 작업 트리 밖에 두고 파일 접근 권한을 제한한다.

```powershell
$env:BALEARN_IDP_ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Keycloak 관리자 비밀번호' -AsSecureString)).Password
$env:OIDC_CLIENT_SECRET = [System.Net.NetworkCredential]::new('', (Read-Host 'Balearn용 OIDC 시크릿' -AsSecureString)).Password
docker compose -f compose.balearn-local.yml -f compose.balearn-oidc.yml up -d
curl.exe -fsS http://localhost:8080/actuator/health
```

1. `http://idp.balearn.localhost:8180/admin/`에서 관리자 `admin`으로 로그인한다. `balearn` 영역의 **Users**에서 사용할 계정을 만들고 이메일과 이메일 확인 상태를 설정한 뒤 비밀번호를 발급한다. 이 Keycloak 개발용 서비스는 루프백에서 실행한다.
2. 별도 PowerShell에서 FE 저장소로 이동하고 `npm.cmd ci`를 실행한다. `NEXT_PUBLIC_API_URL=http://localhost:8080`, `NEXT_PUBLIC_LOGIN_PROVIDERS=oidc`를 설정하고 `npm.cmd run dev -- -H 127.0.0.1 -p 3000`으로 시작한다.
3. `http://localhost:3000`에서 **조직 계정으로 로그인**을 누른다. 인증 후 대시보드, 모임 생성, 파일 업로드·다운로드를 확인한다.

기존 Kakao 로그인도 함께 쓸 경우 FE에 `NEXT_PUBLIC_LOGIN_PROVIDERS=oidc,kakao`, 백엔드에 `KAKAO_CLIENT_ID`와 `KAKAO_CLIENT_SECRET`을 함께 설정한다. OIDC는 검증된 이메일을 필요로 하며, 기존 소셜 계정과 이메일이 겹쳐도 자동으로 계정을 연결하지 않는다. 자체 호스팅 IdP 구성이 바뀌면 발급자 URL과 사용자 식별자 변경에 따른 계정 이전을 별도로 계획한다.

## 온프레미스에서 컨테이너로 배포

이 PC의 VMware에서 외부 TCP 9443을 쓰는 새 VM 배포는 [VM 배포 가이드](VM_DEPLOYMENT.md)를 따른다.

1. 작업 트리 밖의 Compose 환경 파일에 `BACKEND_URL`, `FRONTEND_URL`의 외부 HTTPS 주소, `JWT_SECRET`의 Base64 서명 키, `MYSQL_ROOT_PASSWORD`, `DATABASE_PASSWORD`, `REDIS_PASSWORD`를 설정한다. 브라우저가 사용하는 프런트엔드 주소를 `WEB_ALLOWED_ORIGINS`에 지정한다. 쿠키를 API 호스트에만 쓰려면 `FRONTEND_DOMAIN`은 비워 둔다.
2. 조직 IdP에 기밀 OIDC 클라이언트를 만들고 `<BACKEND_URL>/login/oauth2/code/oidc`를 등록한다. `OIDC_ISSUER_URL`, `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`을 지정한다. 발급자와 문서의 엔드포인트는 브라우저와 백엔드 컨테이너에서 각각 접근 가능해야 한다. Keycloak을 운영에 쓰는 경우 운영 모드와 영구 DB·TLS로 별도 구성한다.
3. 리버스 프록시에서 웹과 API 도메인을 각각 호스트의 `127.0.0.1:3000`, `127.0.0.1:8080`으로 연결한다. API 프록시는 `/ws`의 WebSocket 업그레이드와 자료 업로드 요청 크기를 지원해야 한다.
4. 백업한 스키마를 복원하고 기본값 `JPA_DDL_AUTO=validate`로 검증한다. 빈 DB의 최초 스키마 생성과 이전 DB의 사용자 제공자 열에 `OIDC`를 추가하는 작업은 백업 사본에서 먼저 확인한다. Hibernate로 변경을 적용할 경우 한 차례 `JPA_DDL_AUTO=update`로 시작한 뒤 `validate`로 복원한다.

```powershell
docker compose --env-file <환경파일> -p balearn-onprem -f compose.balearn-local.yml -f compose.balearn-onprem.yml config --quiet
docker compose --env-file <환경파일> -p balearn-onprem -f compose.balearn-local.yml -f compose.balearn-onprem.yml up -d --build
```

프록시가 제공하는 외부 API·미디어 주소는 브라우저와 Next.js 이미지 최적화 컨테이너 양쪽에서 접근 가능해야 한다. `NEXT_PUBLIC_*`와 `MEDIA_IMAGE_BASE_URLS`는 FE 빌드 시 반영된다. 운영 데이터의 MySQL, Elasticsearch, Redis, 미디어 볼륨과 IdP 상태를 각 서비스의 복원 절차에 맞춰 백업한다. 외부망이 제한된 곳에서는 필요한 앱 이미지와 기반 서비스 이미지를 내부 레지스트리로 가져온 후 배포한다.

## 클라우드나 외부 서비스 전환

- **파일 저장**은 `MEDIA_STORAGE_PROVIDER=gcs`로 전환한다. 공개 프로필 이미지의 버킷과 HTTPS 기준 주소를 입력하고, 자료용 비공개 버킷을 `GCS_LEARNING_BUCKET`으로 별도 지정한다. GCP 런타임의 워크로드 자격 증명(ADC)이나 `GCS_KEY`를 주입한다. 프런트엔드의 `MEDIA_IMAGE_BASE_URLS`에 이미지 CDN 기준 주소를 넣고 이미지를 다시 빌드한다. DB에는 객체의 저장 위치가 기록된다. 모임 자료는 인증과 멤버 권한을 검증하는 `/api/file/{id}/download`로 내려받는다. 기존 공개 버킷 자료를 이전할 때는 객체를 비공개 버킷으로 복사하고 DB 참조와 이전 공개 URL의 접근 가능 여부를 함께 확인한다.
- **데이터 서비스**는 `DATABASE_URL`·`DATABASE_USER`·`DATABASE_PASSWORD`, `REDIS_HOST`·`REDIS_PORT`·`REDIS_USERNAME`·`REDIS_PASSWORD`·`REDIS_TLS`, `ES_URL`·`ES_USERNAME`·`ES_PASSWORD`로 교체한다. 외부 서비스를 쓸 때는 필요한 로컬 데이터 서비스만 먼저 기동하고, 앱 서비스는 `up -d --no-deps backend frontend`로 시작한다. 기존 데이터를 이동할 때는 서비스별 스냅샷을 복원한 뒤 쓰기 주소를 바꾼다.
- **이미지와 실행 환경**은 `BALEARN_BACKEND_IMAGE`와 `BALEARN_FRONTEND_IMAGE`에 사설 레지스트리 또는 Artifact Registry의 이미지 태그를 지정한다. 레지스트리 인증 후 `docker compose pull backend frontend`와 `up -d --no-build`를 사용한다. 기존 `asia.gcr.io` 주소는 해당 프로젝트의 Artifact Registry 이전 상태를 확인한 뒤 사용한다. [Google Cloud 레지스트리 이전 문서](https://docs.cloud.google.com/artifact-registry/docs/transition/transition-from-gcr)
- **수평 확장** 시 로컬 디스크를 쓰는 노드는 동일 미디어 볼륨을 공유하거나 GCS로 전환해야 한다. OAuth 인가 요청이 사용하는 세션에 대한 라우팅과 기존 Redis 채팅 전달 및 예약 작업의 동시 실행을 검증한 뒤 백엔드 복제본을 추가한다.

외부망이 없는 환경에 과거 데이터를 복원할 때는 CDN의 업로드 파일과 소셜 제공자 프로필 이미지의 원본이 필요한지 함께 확인한다. 기본 이미지의 이전 CDN URL은 앱에 포함된 기본 이미지로 매핑된다.

개별 자료를 삭제할 때는 DB에 기록된 URL의 공개 기준 주소가 현재 서버가 설정한 로컬 또는 GCS 기준 주소와 일치해야 한다. 전환 전 버킷에 남은 자료를 삭제하려면 해당 GCS 저장소를 구성하거나 마이그레이션에서 URL을 갱신한다. GCS를 쓸 때 서비스 계정에 대상 객체 삭제 권한이 필요하다. 과거 CDN에 캐시된 응답은 운영 환경의 캐시 정책에 따라 별도로 무효화한다.

## 재현 검증 기록

- 격리된 Docker 스택에서 GCP 자격 증명 없이 백엔드 헬스 체크, Keycloak 인가 코드 콜백, Balearn 토큰 발급, 모임 생성, 팀 이미지 조회와 자료 다운로드를 확인했다. 저장한 이미지와 자료는 다운로드 후 원본과 일치했다.
- FE와 BE 배포 이미지를 빌드했다. 기존 로컬 스택은 새 백엔드에서 헬스 체크와 기존 카카오 인증 리디렉션이 성공했다.
- 실제 조직의 IdP·데이터 서비스·TLS 프록시와 GCS 버킷 연결은 대상 환경에서 확인해야 한다. 기존 클라우드 데이터와 파일은 이번 작업에서 이전하지 않았다.
