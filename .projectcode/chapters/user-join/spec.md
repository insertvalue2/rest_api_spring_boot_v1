# user-join 회원가입

종류: 기능
배울 것: Bean Validation, Security 기본 설정, BCrypt, 아이디 중복 확인

## 할 일
- S1 Security를 넣어도 글 목록, 글 상세, H2 콘솔은 로그인 없이 그대로 열린다
- S2 POST /join이 아이디, 비밀번호, 이메일을 받아 회원으로 저장하고 id, username, email을 돌려준다
- S3 저장된 비밀번호는 BCrypt로 암호화돼 원문과 다르다
- S4 입력이 틀리면 400과 이유 문구만 돌려준다 (빈 값, 아이디와 비밀번호 4~20자, 이메일 형식)
- S5 이미 있는 아이디로 가입하면 400과 "이미 존재하는 유저네임입니다"를 돌려준다
- S6 GET /check-username?username=ssar은 available false, 없는 아이디는 true를 돌려준다
- S7 check-username에 username을 빼면 400과 "username:값을 입력해주세요"를 돌려준다

## 요청과 응답
POST /join
요청: { "username": "love", "password": "1234", "email": "love@nate.com" }
응답: { "status": 200, "msg": "성공", "body": { "id": 3, "username": "love", "email": "love@nate.com" } }

POST /join (비밀번호 12)
응답: { "status": 400, "msg": "비밀번호는 4~20자여야 합니다", "body": null }

GET /check-username?username=ssar
응답: { "status": 200, "msg": "성공", "body": { "available": false } }

## 흐름
POST /join -> 입력 검사 -> 아이디 중복 검사 -> 비밀번호 암호화 -> 저장 -> 회원 정보 응답
GET /check-username -> 아이디 있는지 조회 -> available 응답

## 안 하는 것
- 로그인: user-login 장
- 회원 역할(USER, ADMIN): admin-api 장
- CORS: user-login 장
