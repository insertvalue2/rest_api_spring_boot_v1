# board-list 글 목록

종류: 기능
배울 것: JPA 엔티티, ManyToOne, Repository, data.sql, 공통 응답 Resp, 응답 DTO

## 할 일
- S1 서버를 띄우면 회원 2명(ssar, cos)과 글 5개가 DB에 들어간다
- S2 GET /api/boards가 모든 글을 작성자 아이디와 함께 돌려준다
- S3 글 목록은 최신 글(번호 큰 글)부터 온다
- S4 응답은 status, msg, body 세 칸으로 감싸서 온다

## 요청과 응답
GET /api/boards
응답:
{
  "status": 200,
  "msg": "성공",
  "body": [
    { "id": 5, "title": "제목5", "content": "내용5", "username": "cos", "createdAt": "..." },
    { "id": 4, "title": "제목4", "content": "내용4", "username": "cos", "createdAt": "..." },
    { "id": 3, "title": "제목3", "content": "내용3", "username": "ssar", "createdAt": "..." }
  ]
}

## 흐름
서버 실행 -> 테이블 생성 -> 샘플 데이터 입력 -> GET /api/boards -> 글과 작성자 조회 -> 최신순 목록 -> 공통 응답으로 감싸기

## 안 하는 것
- 쪽 나누기와 검색: board-paging 장
- 글 상세: board-detail 장
- 회원 권한(역할) 데이터: admin-api 장
- 실패 응답 모양: board-detail 장
