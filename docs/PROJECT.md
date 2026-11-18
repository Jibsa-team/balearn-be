# Balearn 프로젝트 정의와 유지보수 가이드

Balearn은 학습 모임 구성원이 목표와 미션을 정하고, 일정을 관리하며, 자료와 대화를 공유하는 팀 단위 학습 협업 서비스다. 대시보드와 순위 화면은 팀의 활동과 참여 결과를 보여준다. 팀 생성과 초대, 구성원 권한, 공지와 프로필이 모임 운영을 지원한다.

이 문서의 시작 기준은 프런트엔드 `81da030`, 백엔드 `dc13c36`이다. 개발 브랜치의 로컬·온프레미스·클라우드 설정은 [배포 가이드](DEPLOYMENT.md)를 참고한다.

## 저장소와 책임

| 저장소 | 책임 | 과거 기준 | 개발 브랜치 |
|---|---|---|---|
| [Jibsa-team/balearn-fe](https://github.com/Jibsa-team/balearn-fe) | 웹 화면, 브라우저 상태, REST 및 실시간 채팅 클라이언트 | `develop` · `81da030` | `develop-2026` |
| [Jibsa-team/balearn-be](https://github.com/Jibsa-team/balearn-be) | 인증과 팀 권한, 비즈니스 로직, 저장소 연동, 배포 설정 | `develop` · `dc13c36` | `develop-2026` |

## 구성과 아키텍처

- **프런트엔드**는 Next.js 14.2.18 App Router, React 18, TypeScript 5 기반이다. `src/app/(with-header)`의 대시보드, 캘린더, 성취, 자료, 채팅, 설정 라우트가 모임별 화면을 구성한다. 공유 UI는 `src/components`, 화면 로직은 `src/hooks`, 계약 타입은 `src/types`에 있다. TanStack Query로 서버 상태를 다루고 Zustand에 인증·모임·파일 관련 상태를 둔다. 일부 REST 호출은 `src/app/api`에 있으며 브라우저에서 백엔드로 직접 요청한다. Tailwind와 Radix 기반 UI를 사용하며 MUI 의존성도 있다. PWA 플러그인과 서비스 워커 산출물이 있다.
- **백엔드**는 Kotlin 1.9.25, Spring Boot 3.3.5, Java 17, Gradle Wrapper 8.11.1로 구성된 단일 Spring 애플리케이션이다. `com.jipsa.balearn` 아래에서 `api`가 REST/STOMP 요청과 DTO를, `domain`이 서비스와 도메인 규칙을, `database`가 엔티티와 저장소 어댑터를, `infra`가 OAuth, Redis, Elasticsearch, GCS, WebSocket 설정을 담당한다. 공통 API 응답 형식은 `common/api/ApiResponse.kt`를 확인한다.
- **인증과 권한**은 Spring Security, OAuth 설정(Google·Kakao·GitHub), JWT를 사용한다. 프런트엔드 `fetchWithAuth`는 Bearer 토큰을 붙이고 `/api/auth/reissue`로 재발급을 시도한다. 로그인 변경은 콜백 주소, 쿠키 속성, CORS, 팀별 권한 확인까지 함께 검증한다.
- **저장과 실시간 통신**에서 MySQL/JPA는 팀·구성원·일정 등 관계형 데이터를 담당하고, 파일은 설정에 따라 로컬 디스크 또는 GCS에 저장한다. 채팅은 SockJS/STOMP와 서버 내 브로커를 사용한다. 연결은 `/ws`, 발행과 구독 접두사는 각각 `/pub`, `/sub`다. 메시지는 Redis에 기록되고 예약 작업이 Elasticsearch에 일괄 적재한다. 채팅 이력 변경 시 커서 경계, 중복, 순서 및 일괄 적재 실패 후 재시도를 확인한다.

## 환경과 실행 기준

| 항목 | 새 환경에서 확인할 내용 |
|---|---|
| 프런트엔드 | `package-lock.json` 기준으로 `npm ci`, `npm run dev`. `NEXT_PUBLIC_API_URL`을 개발 API 주소로 설정한다. 저장소에서 런타임 고정 파일이 확인되지 않아 Node/npm 버전은 재현 과정에서 결정하고 기록한다. |
| 백엔드 | JDK 17과 `gradlew.bat`을 사용한다. 서버 포트는 8080이다. `application.yml`과 `application-dev.yml`을 기준으로 개발 프로필 및 설정을 점검한 뒤 `bootRun`을 실행한다. |
| 데이터와 외부 서비스 | `docker-compose-local.yml`에는 앱·Redis·Elasticsearch·Kibana·Logstash가 있으며 MySQL은 별도 준비가 필요하다. `DATABASE_*`, `REDIS_*`, `ES_HOST`, OAuth 클라이언트 설정, `JWT_SECRET`, `API_URL`, `BACKEND_URL`, GCP/GCS 설정은 해당 YAML에서 확인하고 개발용 값을 주입한다. |
| 기존 배포 | 백엔드 GitHub Actions는 `develop` 푸시 시 Gradle 빌드에서 테스트를 제외하고 Docker 이미지를 GCR에 올린다. 원격 서버에서 Compose로 다음 색상 컨테이너를 띄워 Actuator 헬스 체크 후 Nginx를 전환한다. 현재 배포 대상과 자격 증명의 유효성은 미확인이다. 프런트엔드 저장소의 추적 파일에서는 배포 워크플로가 확인되지 않았다. |

개발 프로필은 Hibernate `ddl-auto: update`를 사용한다. 새 환경 재현에는 별도 개발 DB를 사용한다. 로컬 기동 기록에는 사용한 버전, 환경 변수 이름, 서비스 시작 순서, 성공한 점검과 실패 원인을 남긴다.

### Windows 로컬 실행

두 저장소의 상위 폴더에 있는 `compose.balearn-local.yml`로 백엔드와 전용 MySQL, Redis, Elasticsearch를 시작한다.

```powershell
docker compose -f compose.balearn-local.yml up -d
docker compose -f compose.balearn-local.yml logs -f backend
curl.exe -fsS http://localhost:8080/actuator/health
```

별도 PowerShell에서 프런트엔드를 시작한다. 이 PC에서는 Node 26.13.0과 npm 11.20.0으로 설치 및 개발 서버 기동을 확인했다.

```powershell
Set-Location C:\Project_Balearn\balearn-fe
npm.cmd ci
$env:NEXT_PUBLIC_API_URL = 'http://localhost:8080'
npm.cmd run dev -- -H 127.0.0.1 -p 3000
```

접속 주소는 `http://localhost:3000`이다. 로컬 파일 저장은 별도 클라우드 자격 증명 없이 동작한다. 외부 계정 없이 로그인하려면 [배포 가이드의 로컬 Keycloak 설정](DEPLOYMENT.md#로컬에서-클라우드-계정-없이-실행)을 적용한다. 기존 소셜 로그인은 개발용 자격 증명과 콜백 등록이 필요하다.

## 작업 방식과 컨벤션

- 새 변경은 각 저장소의 `develop-2026`을 기준으로 작업한다. 과거 `develop`은 비교 기준으로 보존한다. 백엔드의 기존 배포 트리거를 고려해 PR 대상 브랜치를 확인한다.
- 기존 이력에는 `fix : …`, `refactor : …` 커밋과 `feat/#52-test` 브랜치가 있다. 두 저장소의 PR 템플릿은 변경 설명, 연결 이슈, 체크리스트와 리뷰어 메모를 요구한다. 기능·버그·리팩토링·설정 변경용 이슈 템플릿이 있다.
- 프런트엔드 라우트는 `page.tsx`와 동적 세그먼트 `[id]`를 사용한다. 기존 파일명 표기가 혼재하므로 전체 이름 변경은 별도 작업으로 다룬다. 백엔드는 기능별 `*Api`, `*Service`, `*RepositoryAdaptor` 패턴을 따른다.
- API 변경 시 백엔드 DTO·응답·권한과 프런트엔드 호출부·타입·오류 처리를 같은 변경 단위에서 확인한다. 팀 식별자와 사용자 식별자를 구별하고, 일정과 채팅 시간은 직렬화 및 시간대 동작을 확인한다.

## 유지보수 순서

1. **실행 기준 확보.** 깨끗한 체크아웃에서 FE의 `npm run lint`, `npm run build`와 BE의 `.\gradlew.bat test`, `.\gradlew.bat build`를 확인한다. 현재 BE 테스트는 Spring 컨텍스트 로딩이 중심이고 FE에는 테스트 실행 스크립트가 없다. 환경 설정 실패와 기능 실패를 구분해 기록한다.
2. **핵심 흐름 고정.** 개발 데이터로 로그인 → 팀 생성·가입 → 일정 또는 미션 확인을 재현한다. 이어 권한 거부, 자료 업로드·다운로드, 채팅 발송 및 이력 조회를 점검한다.
3. **작은 단위로 개선.** 재현에서 드러난 설정·인증·권한 문제를 먼저 해결한다. 변경할 경로에 회귀 검증을 추가한 뒤 중복 API 호출, 날짜 처리 또는 채팅 저장 경계처럼 범위를 좁혀 리팩토링한다. 기존 배포 스크립트의 Docker 소켓 권한 변경과 테스트 생략은 새 환경에 배포하기 전에 검토한다.

제품 범위, 실행 절차, 저장소 간 계약, 배포 방식이 바뀌면 해당 PR에서 이 문서를 갱신한다. 설정값이나 실제 비밀은 기록하지 않는다.

로컬 기능 테스트의 실행 절차와 결과는 [로컬 기능 검증](LOCAL_QA.md)에 기록한다.
