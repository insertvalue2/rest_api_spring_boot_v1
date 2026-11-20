# board-paging 목록 쪽 나누기와 검색

종류: 기능
배울 것: Pageable, Page, 제목 검색 쿼리

## 할 일
- S1 GET /api/boards?page=0이 최신 글 3개를 준다. page는 0부터 센다
- S2 page를 주지 않으면 첫 쪽(page=0)을 준다
- S3 목록과 함께 쪽 정보를 준다: 지금 쪽 번호(number), 전체 쪽 수(totalPage), 첫 쪽인지(isFirst), 마지막 쪽인지(isLast)
- S4 keyword를 주면 제목에 그 글자가 들어간 글만 준다. 비어 있으면 전체에서 준다

## 요청과 응답
GET /api/boards?page=0
응답:
{
  "status": 200, "msg": "성공",
  "body": {
    "boards": [ { "id": 5, "title": "제목5", "content": "내용5", "username": "cos", "createdAt": "..." }, ...3개 ],
    "number": 0, "totalPage": 2, "isFirst": true, "isLast": false
  }
}

GET /api/boards?page=0&keyword=제목1
응답: body.boards에 제목1 글 하나, totalPage 1, isFirst true, isLast true

## 흐름
GET /api/boards?page&keyword -> 쪽 번호와 크기 3, 최신순으로 쪽 요청 만들기 -> 제목 검색과 작성자 함께 조회 -> 쪽 결과 -> 목록과 쪽 정보 DTO -> 공통 응답

## 안 하는 것
- 한 쪽 글 수 바꾸기(size): 안 만듦
- 내용이나 작성자로 검색: 안 만듦
