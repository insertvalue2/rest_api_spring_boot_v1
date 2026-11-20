# project-setup 프로젝트 뼈대

종류: 기능
배울 것: Gradle 구조, Spring Boot 실행, H2 설정, 테스트 실행

## 할 일
- S1 서버를 띄우면 8080 포트에서 요청을 기다린다
- S2 서버가 H2 메모리 DB(jdbc:h2:mem:test, 사용자 sa)에 연결된다
- S3 H2 콘솔 주소(/h2-console)에 접속하면 로그인 화면이 열린다
- S4 루트에서 테스트 명령 하나로 tests 폴더의 행동 테스트가 돌아간다

## 요청과 응답
GET http://localhost:8080/h2-console
응답: H2 콘솔 로그인 화면 (HTML)

## 흐름
서버 실행 -> H2 메모리 DB 연결 -> 8080 대기 -> 테스트 명령 -> 행동 테스트 실행

## 안 하는 것
- 서버 확인용 주소: 안 만듦
- 공통 응답 모양: board-list 장
- 샘플 데이터: board-list 장
- 보안 설정: user-join 장
