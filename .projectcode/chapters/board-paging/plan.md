# board-paging 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java | 고침 | 목록을 Pageable로 받아 Page로 돌려준다. 제목 검색 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java | 고침 | 쪽 정보 PageDTO를 더한다 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardService.java | 고침 | 쪽 요청(크기 3, 최신순)을 만든다 |
| server-spring/src/main/java/com/newtenco/blog/board/BoardController.java | 고침 | page, keyword 요청 값을 받는다 |

## 코드 계획
BoardRepository.java
- findAllJoinUser가 keyword와 Pageable을 받아 Page<Board>를 돌려준다
- 쿼리에서 order by를 빼고 정렬은 Pageable의 Sort로 옮긴다
- join fetch는 개수 세기에 쓸 수 없어서 countQuery를 따로 쓴다
- 제목 검색: b.title like concat('%', :keyword, '%'). keyword가 비어 있으면 전체

BoardResponse.java
- 글 한 줄은 지금의 ListDTO를 그대로 쓴다
- record PageDTO(List<ListDTO> boards, int number, int totalPage, boolean isFirst, boolean isLast)를 더한다
- Page<Board>를 받는 생성자에서 getContent, getNumber, getTotalPages, isFirst, isLast를 꺼낸다
- 이름: 참고 저장소의 ListDTO(쪽)와 DTO(글) 대신, board-list의 이름을 바꾸지 않으려고 PageDTO를 쓴다

BoardService.java
- 글목록(page, keyword): PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"))

BoardController.java
- @RequestParam(value = "page", defaultValue = "0") Integer page
- @RequestParam(value = "keyword", defaultValue = "") String keyword

## 핵심 코드
@Query(value = "select b from Board b join fetch b.user where b.title like concat('%', :keyword, '%')",
        countQuery = "select count(b) from Board b where b.title like concat('%', :keyword, '%')")
Page<Board> findAllJoinUser(@Param("keyword") String keyword, Pageable pageable);

Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));

public record PageDTO(List<ListDTO> boards, int number, int totalPage, boolean isFirst, boolean isLast) {
    public PageDTO(Page<Board> boardPage) {
        this(boardPage.getContent().stream().map(ListDTO::new).toList(),
                boardPage.getNumber(), boardPage.getTotalPages(), boardPage.isFirst(), boardPage.isLast());
    }
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | BoardRepository.java, BoardService.java, BoardController.java |
| S2 | BoardController.java |
| S3 | BoardResponse.java, BoardService.java |
| S4 | BoardRepository.java, BoardService.java, BoardController.java |

## 테스트 확인 방법
- S1 ?page=0은 id 5, 4, 3, ?page=1은 id 2, 1
- S2 page 없이 요청하면 ?page=0과 같은 글
- S3 page=0에서 number 0, totalPage 2, isFirst true, isLast false
- S4 keyword=제목1이면 글 하나, 제목에 제목1이 들어 있다
- 앞 장 BoardListTest는 body.boards 모양과 첫 쪽 3개에 맞게 고친다 (Touches: board-list)

## 라이브러리
- 추가 없음

## 다른 장 파일
- server-spring/src/main/java/com/newtenco/blog/board/BoardRepository.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardResponse.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardService.java: board-list
- server-spring/src/main/java/com/newtenco/blog/board/BoardController.java: board-list
