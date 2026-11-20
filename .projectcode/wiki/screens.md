# 화면 모음

화면이 없는 예제다. 장 끝 사진은 API 응답 JSON이다.

## GET /h2-console
처음 나온 장: project-setup
장 끝 사진: chapters/project-setup/captures/done-H2-콘솔.json
보이는 것: status가 200이고 H2 콘솔 로그인 화면 HTML이 온다

## GET /api/boards
처음 나온 장: board-list
장 끝 사진: chapters/board-paging/captures/done-쪽-나눈-목록.json
보이는 것: 첫 쪽 글 5, 4, 3과 number 0, totalPage 2, isFirst true, isLast false

## GET /api/boards/{id}
처음 나온 장: board-detail
장 끝 사진: chapters/board-detail/captures/done-없는-글-404.json
보이는 것: 없는 글 99를 요청하면 status 404, msg 게시글을 찾을 수 없습니다, body null이 온다

## POST /join
처음 나온 장: user-join
장 끝 사진: chapters/user-join/captures/done-회원가입.json
보이는 것: love 가입이 200과 id 3, username love, email을 준다

## GET /check-username
처음 나온 장: user-join
장 끝 사진: chapters/user-join/captures/T5-중복-확인.json
보이는 것: ssar은 available false

## POST /login
처음 나온 장: user-login
장 끝 사진: chapters/user-login/captures/done-로그인.json
보이는 것: Origin 5173으로 로그인하면 200, Authorization Bearer 토큰, Access-Control-Expose-Headers Authorization

## GET /api/users/me
처음 나온 장: jwt-auth
장 끝 사진: chapters/jwt-auth/captures/done-토큰-없음.json
보이는 것: 토큰 없이 부르면 401 로그인 후 이용해주세요
