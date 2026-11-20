# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-주소-열기.json | user-join | T1 | cdaa170 | GET /api/boards | Security를 넣은 뒤에도 GET /api/boards가 200으로 글 목록을 주고 X-Frame-Options는 SAMEORIGIN이다 |
| T2-회원가입.json | user-join | T2 | 1060d34 | POST /join | POST /join이 id 3, love, 이메일을 준다 |
| T3-입력-검사.json | user-join | T3 | 09c8f54 | POST /join | 비밀번호 12로 가입하면 400 비밀번호는 4~20자여야 합니다 |
| T4-같은-아이디.json | user-join | T4 | dbb6ff5 | POST /join | ssar로 가입하면 400 이미 존재하는 유저네임입니다 |
| T5-중복-확인.json | user-join | T5 | 5e4e8e6 | GET /check-username?username=ssar | ?username=ssar은 available false |
| T6-값-빠짐.json | user-join | T6 | 561c124 | GET /check-username | /check-username만 부르면 400 username:값을 입력해주세요 |
| done-회원가입.json | user-join | done | 561c124 | POST /join | love로 가입하면 id 3, love, 이메일을 준다 |
