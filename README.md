# 삼척 친환경/체험형 분산 관광 가이드 시스템 (ecotour)

삼척의 자연환경(해안·산간 계곡)을 보존하면서, 쏠비치·장호항 등 특정 명소로의 **쏠림(병목)** 을 완화하고
숨겨진 로컬 명소로 관광객을 유도하는 **분산 관광(distributed tourism)** 백엔드 시스템.

> P3L 기반 Spring 프로젝트 / **TDD(테스트 주도 개발)** 로 구현.

## 기술 스택
- Java 17 (빌드 JDK 24), Spring Boot 3.5.6, Maven
- Spring Web, Spring Data JPA, Bean Validation, H2(in-memory)
- 테스트: JUnit 5, Mockito, AssertJ, Spring MockMvc

## 필요 프로그램 (버전 · 링크)
| 프로그램 | 버전 | 다운로드 |
|---|---|---|
| JDK (Java) | Oracle JDK **24.0.2** (최소 17) | https://www.oracle.com/java/technologies/downloads/ |
| Apache Maven | **3.9.16** (또는 동봉된 `./mvnw`) | https://maven.apache.org/download.cgi |
| Spring Boot | **3.5.6** (Maven 자동 관리) | https://spring.io/projects/spring-boot |
| H2 Database | 2.x in-memory (Maven 자동 관리) | https://www.h2database.com |
| Git (버전관리) | 최신 | https://git-scm.com |
| Eclipse IDE (선택) | 2026-03 | https://www.eclipse.org/downloads/packages/ |

전체 목록·빠른 시작은 [`docs/03-requirements.md`](docs/03-requirements.md) 참고.

## 아키텍처 (계층형)
```
controller (REST/검증)  →  service (비즈니스 로직/트랜잭션)  →  repository (JPA)  →  domain (엔티티)
                  ▲ dto(요청/응답)         ▲ exception(전역 예외 처리)
```

## 실행 방법
```powershell
# 프로젝트 폴더에서 (전역 Maven 또는 ./mvnw)
mvn spring-boot:run
```
- 웹 UI: http://localhost:8080/ (프론트엔드 단일 페이지)
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:ecotour`, user: `sa`)
- 8080이 사용 중이면(예: Oracle TNS): `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8090`
- 실행 시 `data.sql` 의 삼척 관광지 시드 데이터가 자동 적재됩니다.

> 프론트엔드는 빌드툴 없는 순수 HTML/CSS/JS(`src/main/resources/static/`)로, 실 API를 호출하되 API가 없으면 임베드 시드 데이터로 폴백됩니다.

## 주요 API (관광지 슬라이스)
| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/api/attractions` | 관광지 등록 (검증) |
| GET | `/api/attractions/{id}` | 단건 조회 (없으면 404) |
| GET | `/api/attractions?category=COAST` | 전체/카테고리별 조회 |
| PATCH | `/api/attractions/{id}/congestion` | 실시간 혼잡도 갱신 |
| GET | `/api/attractions/{id}/alternatives` | **분산 관광 대안 추천** |

### 분산 관광 추천 로직 (`recommendAlternatives`)
기준 관광지와 **같은 카테고리**의 다른 관광지 중에서
1. 기준보다 **덜 붐비거나(혼잡도↓)** 또는 **숨겨진 로컬 명소**인 곳만 골라
2. `로컬 명소 우선 → 혼잡도 낮은 순 → 이름 순` 으로 정렬해
3. 최대 3곳을 추천

예) `쏠비치(COAST·HIGH)` 조회 → 붐비는 `장호항`은 제외하고 한적한 로컬 명소 `초곡용굴촛대바위길`, `덕봉산 해안생태탐방로` 추천.

## 테스트 (TDD)
```powershell
mvn test
```
- `RED → GREEN` 사이클로 개발: 테스트 작성 → 실패 확인 → 구현 → 통과.
- 슬라이스 테스트: `@DataJpaTest`(리포지토리), Mockito(서비스), `@WebMvcTest`(컨트롤러) + 통합 `@SpringBootTest`.

## 평가 루브릭 매핑
| 영역 | 충족 내용 |
|---|---|
| 1. 문제 정의·기획 | `docs/01-planning.md` — 지역 현안 → 기능 요구사항 |
| 2. DB 설계 | `docs/02-database-design.md` — 6개 엔티티, PK/FK·정규화, ERD |
| 3. Spring 비즈니스 로직 | 계층형 구조 + 조건/예외 처리 + 분산 추천 등 복합 로직 |
| 4. 협업·발표 | Git 버전관리, 본 README, 발표 시연 시나리오 |

## 구현 현황 (모든 슬라이스 TDD 완료 · 64 tests GREEN)
- [x] 관광지(Attraction) — CRUD, 실시간 혼잡도, **분산 관광 추천**
- [x] 사용자(User) — 회원가입(이메일 중복 409), 선호 테마, 보유 스탬프 집계
- [x] 스탬프 코스(Course/CourseDetail) — 테마별 경로, 방문 순서
- [x] 방문/인증(VisitLog) — 스탬프(중복 409)·친환경 활동(반복 허용) 인증
- [x] 리뷰/평점(Review) — 평점 1~5 검증, 관광지 평균 평점 집계
- [x] 댓글(Comment) — 로그인 없이 닉네임+내용으로 각 관광지에 댓글, 최신순 조회 (프론트엔드 드로어 연동, XSS 방지 렌더링)

### 전체 API 요약
| 도메인 | 엔드포인트 |
|---|---|
| 관광지 | `POST/GET /api/attractions`, `GET /{id}`, `PATCH /{id}/congestion`, `GET /{id}/alternatives` |
| 사용자 | `POST/GET /api/users`, `GET /{id}` |
| 코스 | `POST/GET /api/courses`(`?theme=`), `GET /{id}` |
| 방문·인증 | `POST /api/visits`, `GET /api/visits?userId=`, `GET /api/visits/stamp-count?userId=` |
| 리뷰 | `POST /api/reviews`, `GET /api/reviews?attractionId=`, `GET /api/reviews/average?attractionId=` |
| 댓글 | `POST /api/attractions/{id}/comments`, `GET /api/attractions/{id}/comments` |

## 남은 작업
- [ ] Git 버전관리 (현재 git 미설치 — 설치 후 초기 커밋, 루브릭 4)
- [ ] (선택) 통합 테스트 확대 / 프런트엔드 / MySQL 프로파일
