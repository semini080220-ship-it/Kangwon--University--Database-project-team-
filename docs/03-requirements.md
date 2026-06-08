# 필요 프로그램 · 버전 · 다운로드 링크

이 프로젝트를 빌드·실행하는 데 필요한 모든 소프트웨어와 버전, 공식 다운로드 링크.

## 필수 (빌드·실행)

| 구분 | 프로그램 | 사용 버전 | 최소 요구 | 다운로드 |
|---|---|---|---|---|
| 언어/런타임 | **JDK (Java)** | Oracle JDK **24.0.2** | Java **17+** | https://www.oracle.com/java/technologies/downloads/ · (대안) https://adoptium.net |
| 빌드 도구 | **Apache Maven** | **3.9.16** | 3.6.3+ | https://maven.apache.org/download.cgi |
| 프레임워크 | **Spring Boot** | **3.5.6** | — (Maven이 자동 관리) | https://spring.io/projects/spring-boot |
| 데이터베이스 | **H2 Database** (in-memory) | 2.x (Boot 관리) | — | https://www.h2database.com |

> **Maven Wrapper 포함**: 전역 Maven이 없어도 프로젝트의 `./mvnw` (Windows: `mvnw.cmd`)로 동일하게 빌드할 수 있습니다. 이 경우 JDK만 있으면 됩니다.

## Maven이 자동으로 받는 의존성 (직접 설치 불필요)
- `spring-boot-starter-web` — REST API
- `spring-boot-starter-data-jpa` — JPA/Hibernate
- `spring-boot-starter-validation` — Bean Validation
- `com.h2database:h2` — 인메모리 DB
- `spring-boot-starter-test` (JUnit 5, Mockito, AssertJ, Spring Test)

## 프론트엔드 (빌드툴 없음)
- 순수 **HTML/CSS/JavaScript** (`src/main/resources/static/`), 별도 설치/빌드 불필요.
- 웹폰트(Google Fonts, CDN 자동 로드): **Gowun Batang**, **IBM Plex Sans KR**, **IBM Plex Mono**.
- 최신 브라우저(Chrome/Edge/Firefox) 권장.

## 선택 (개발 편의)
| 구분 | 프로그램 | 버전 | 다운로드 |
|---|---|---|---|
| 버전관리 | **Git** | 최신 | https://git-scm.com/download/win |
| IDE | **Eclipse IDE for Enterprise Java and Web Developers** | 2026-03 | https://www.eclipse.org/downloads/packages/ |
| IDE | IntelliJ IDEA | 최신 | https://www.jetbrains.com/idea/ |

## 빠른 시작
```bash
# 1) 빌드 + 테스트
mvn test                # 또는  ./mvnw test

# 2) 실행 (기본 8080 포트)
mvn spring-boot:run     # 또는  ./mvnw spring-boot:run
#   8080이 사용 중이면:  mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8090

# 3) 접속
#   웹 UI :  http://localhost:8080/
#   H2 콘솔:  http://localhost:8080/h2-console   (JDBC URL: jdbc:h2:mem:ecotour, user: sa)
```
