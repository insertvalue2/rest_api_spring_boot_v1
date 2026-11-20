# 프로젝트 규칙

## 기술
| 항목 | 선택 |
|---|---|
| 언어 | Java 21 |
| 백엔드 | Spring Boot 4.1.2 |
| 빌드 | Gradle (wrapper) |
| 저장소 | H2 메모리, Spring Data JPA |
| 보안 | Spring Security, java-jwt 4.5.0, BCrypt |
| 기타 | Lombok, Validation |
| API 테스트 | JUnit 5, MockMvc |

## 폴더
| 무엇 | 폴더 |
|---|---|
| 학생 코드 | server-spring |
| 서버 | server-spring |

## 실행
- 서버: cd server-spring, gradlew.bat bootRun (8080)
- 테스트: gradlew.bat test (루트. tests가 server-spring을 포함해 MockMvc로 돌린다)
- 빌드: gradlew.bat build

## 캡처
| 종류 | 주소나 명령 |
|---|---|
| api | http://localhost:8080 |
