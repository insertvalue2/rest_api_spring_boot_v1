# user-login 로그인과 JWT 발급

종류: 기능
배울 것: JWT 만들기, 응답 헤더 Authorization, CORS 노출 헤더

## 할 일
- S1 POST /login이 맞는 아이디와 비밀번호를 받으면 200과 회원 정보(id, username, email)를 돌려준다
- S2 로그인 응답 헤더 Authorization에 "Bearer "로 시작하는 JWT를 담아 준다
- S3 JWT 안에는 회원 번호(id), 아이디(username), 만료 시각(1일 뒤)이 들어 있다
- S4 없는 아이디이거나 비밀번호가 틀리면 401과 "유저네임 혹은 비밀번호가 일치하지 않습니다"를 돌려준다
- S5 아이디나 비밀번호를 비우면 400과 "~를 입력해주세요"를 돌려준다
- S6 리액트 주소(localhost:5173, 3000)와 크롬 확장(Talend API Tester 같은)에서 온 요청을 허락하고, 브라우저 코드가 Authorization 응답 헤더를 읽을 수 있게 노출한다

## 요청과 응답
POST /login
요청: { "username": "ssar", "password": "1234" }
응답 헤더: Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
응답: { "status": 200, "msg": "성공", "body": { "id": 1, "username": "ssar", "email": "ssar@metacoding.com" } }

POST /login (비밀번호 틀림)
응답: { "status": 401, "msg": "유저네임 혹은 비밀번호가 일치하지 않습니다", "body": null }

## 흐름
POST /login -> 입력 검사 -> 아이디로 회원 찾기 -> 비밀번호 비교 -> JWT 만들기 -> 헤더에 담고 회원 정보 응답

## 안 하는 것
- 토큰으로 요청 확인하기(인증 필터): jwt-auth 장
- 토큰 안의 역할(roles): admin-api 장
- 리프레시 토큰: refresh-token 장
