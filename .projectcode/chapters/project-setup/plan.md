# project-setup 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/settings.gradle | 새로 | 프로젝트 이름 blog |
| server-spring/build.gradle | 새로 | Spring Boot 4.1.2, Java 21, 이 장의 의존성 |
| server-spring/gradlew, gradlew.bat, gradle/wrapper | 새로 | Gradle 9.2.1 wrapper |
| server-spring/.gitignore | 새로 | build, .gradle, .idea 제외 |
| server-spring/src/main/java/com/newtenco/blog/BlogApplication.java | 새로 | 서버 시작점 |
| server-spring/src/main/resources/application.properties | 새로 | 포트, H2, JPA 설정 |
| settings.gradle | 새로 | server-spring을 includeBuild, tests를 include |
| gradlew, gradlew.bat, gradle/wrapper | 새로 | 루트 테스트 명령용 wrapper |
| .gitignore | 새로 | 루트 build, .gradle 제외 |
| tests/build.gradle | 새로 | com.newtenco:blog에 의존하는 테스트 프로젝트 |

## 코드 계획
server-spring/build.gradle
- 플러그인은 java, org.springframework.boot 4.1.2, io.spring.dependency-management
- group은 com.newtenco, Java toolchain 21
- 의존성은 webmvc, data-jpa, h2console, h2, lombok, devtools만 넣는다
- Security, java-jwt, validation은 그것을 배우는 장에서 더한다

BlogApplication.java
- @SpringBootApplication과 main 하나

application.properties
- 한 파일에 쓴다. 프로필은 나누지 않는다
- server.port 8080, UTF-8 인코딩
- H2 메모리 jdbc:h2:mem:test, 사용자 sa, 비밀번호 없음, H2 콘솔 켜기
- ddl-auto create, show-sql, format_sql, open-in-view false
- 학생 코드라 주석을 달지 않는다

루트 settings.gradle
- includeBuild("server-spring")로 학생 프로젝트를 포함한다
- include("tests")로 테스트 프로젝트를 더한다
- 나누는 이유: 학생 build.gradle에 테스트 설정을 넣지 않고 tests 폴더를 따로 두기 위해서다

tests/build.gradle
- java, dependency-management 플러그인과 Spring Boot BOM
- testImplementation com.newtenco:blog, spring-boot-starter-test, spring-boot-starter-webmvc-test
- useJUnitPlatform

## 핵심 코드
server.port=8080
spring.datasource.url=jdbc:h2:mem:test
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create

includeBuild("server-spring")
include("tests")

testImplementation "com.newtenco:blog"

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BlogApplication.java, application.properties, server-spring/build.gradle |
| S2 | application.properties, server-spring/build.gradle |
| S3 | application.properties, server-spring/build.gradle |
| S4 | settings.gradle, gradlew, tests/build.gradle |

## 테스트 확인 방법
- S1 서버 설정의 포트 값이 8080이다
- S2 DataSource 연결 URL이 jdbc:h2:mem:test다
- S3 임의 포트로 띄워 GET /h2-console이 200과 HTML을 준다
- S4 루트 gradlew.bat test가 tests의 테스트를 돌린다

## 라이브러리
- 추가 없음 (contract.md 안에서 이 장에 필요한 것만)

## 다른 장 파일
- 없음
