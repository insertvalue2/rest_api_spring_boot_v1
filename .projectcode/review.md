# 따라치기 코드 리뷰

## project-setup T1

### build.gradle

#### 개념 Gradle 구조
##### 무엇
- Gradle은 라이브러리를 내려받고 코드를 빌드하고 실행하는 도구다.
- build.gradle에 어떤 플러그인과 라이브러리를 쓸지 적는다.
- settings.gradle에는 프로젝트 이름을 적는다.
- gradlew는 Gradle을 따로 설치하지 않아도 정해진 버전으로 실행해 주는 파일이다. 시작 키트에 들어 있다.
##### 왜
- jar 파일을 손으로 받아 넣으면 버전이 서로 맞는지 직접 챙겨야 한다.
- Gradle은 이름과 버전만 적으면 필요한 jar를 모두 받아 온다.
##### 쉬운 방법과 비교
- 컴퓨터에 설치된 gradle 명령으로 실행해도 지금은 된다.
- 학생마다 Gradle 버전이 다르면 Spring Boot 4가 빌드되지 않을 수 있다. 그래서 늘 gradlew로 실행한다.
##### 헷갈리는 개념
- Maven도 같은 일을 하는 도구다. pom.xml에 XML로 적는다는 점만 다르다.
##### 이 예제에서는
- server-spring 폴더에서 gradlew bootRun을 실행하면 서버가 뜬다.

#### 코드
- id 'org.springframework.boot'는 bootRun처럼 Spring Boot를 실행하는 작업을 더한다.
- id 'io.spring.dependency-management'는 Spring Boot가 고른 라이브러리 버전을 대신 정해 준다. 그래서 dependencies에 버전을 쓰지 않는다.
- group과 version은 이 프로젝트를 빌드한 결과물에 붙는 이름표다.
- toolchain의 languageVersion 21은 자바 21로 컴파일하라는 뜻이다.
- mavenCentral()은 라이브러리를 받아 올 저장소다.
- spring-boot-starter-webmvc는 컨트롤러와 내장 톰캣을 한 번에 넣는다.
- implementation은 컴파일과 실행 모두에 쓰는 라이브러리다.
- compileOnly와 annotationProcessor로 넣은 lombok은 컴파일할 때 getter와 생성자를 만들어 주고 실행할 때는 빠진다.
- developmentOnly의 devtools는 코드를 고치면 서버를 다시 띄워 준다. 개발할 때만 쓴다.

### settings.gradle

#### 코드
- rootProject.name = 'blog'는 빌드 결과물 이름이 된다. 예를 들어 blog-0.0.1-SNAPSHOT.jar가 만들어진다.

### src/main/java/com/newtenco/blog/BlogApplication.java

#### 개념 Spring Boot 실행
##### 무엇
- main 메서드에서 SpringApplication.run을 부르면 내장 톰캣이 뜨고 스프링이 객체를 만들어 연결한다.
- @SpringBootApplication은 이 클래스가 있는 패키지와 그 아래를 훑어서 @RestController, @Service 같은 클래스를 찾는다.
##### 왜
- 예전에는 톰캣을 따로 설치하고 war 파일을 올려야 했다.
- Spring Boot는 톰캣을 안에 품고 있어서 main 하나로 서버가 뜬다.
##### 헷갈리는 개념
- 이 클래스보다 바깥 패키지에 만든 클래스는 찾지 못한다. 그래서 board, user 같은 패키지를 모두 com.newtenco.blog 아래에 둔다.
##### 이 예제에서는
- 실행하면 8080 포트에서 서버가 기다린다.

### src/main/resources/application.properties

#### 코드
- server.port=8080은 서버가 기다릴 포트다.
- server.servlet.encoding.charset=UTF-8과 force=true는 요청과 응답의 한글이 깨지지 않게 한다.

## project-setup T3

### build.gradle

#### 코드
- spring-boot-starter-data-jpa는 JPA와 Hibernate, Spring Data JPA를 넣는다. 다음 장부터 엔티티와 Repository를 쓴다.
- spring-boot-h2console은 브라우저에서 DB를 들여다보는 H2 콘솔을 켜 준다. Spring Boot 4부터 따로 넣어야 한다.
- runtimeOnly 'com.h2database:h2'는 실행할 때만 필요한 H2 DB 드라이버다. 코드에서 H2 클래스를 직접 부르지 않는다.

### src/main/resources/application.properties

#### 개념 H2 설정
##### 무엇
- H2는 자바로 만든 작은 DB다.
- 메모리 모드로 띄우면 서버가 켜질 때 생기고 꺼지면 사라진다.
##### 왜
- MySQL을 설치하지 않아도 바로 SQL을 실행해 볼 수 있다.
- 서버를 다시 켤 때마다 깨끗한 데이터로 시작하므로 테스트가 늘 같은 결과를 낸다.
##### 쉬운 방법과 비교
- 파일 모드(jdbc:h2:file:...)로 하면 데이터가 남는다.
- 그러면 테스트할 때마다 앞에서 넣은 글과 회원이 쌓여 결과가 달라진다. 이 강의는 메모리 모드로 끝까지 간다.
##### 헷갈리는 개념
- H2 콘솔은 DB가 아니라 DB를 보는 화면이다. 콘솔을 꺼도 DB는 돈다.
##### 이 예제에서는
- 서버를 띄우고 localhost:8080/h2-console에 들어가 JDBC URL에 jdbc:h2:mem:test를 넣으면 테이블을 볼 수 있다.

#### 코드
- spring.datasource.url=jdbc:h2:mem:test는 test라는 이름의 메모리 DB에 연결한다.
- username=sa와 빈 password는 H2의 기본 계정이다.
- spring.h2.console.enabled=true는 h2-console 주소를 연다.
- spring.jpa.hibernate.ddl-auto=create는 서버가 켜질 때 엔티티를 보고 테이블을 지우고 새로 만든다. 다음 장에서 엔티티를 만들면 테이블이 저절로 생긴다.
- spring.jpa.show-sql=true와 format_sql=true는 JPA가 만든 SQL을 콘솔에 보기 좋게 찍는다.
- spring.jpa.open-in-view=false는 DB 연결을 서비스 안에서만 쓰게 한다. 그래서 컨트롤러에서 지연 로딩을 하면 오류가 난다. 엔티티를 서비스 안에서 DTO로 바꾸는 이유가 된다.

## board-list T1

### src/main/java/com/newtenco/blog/board/Board.java

#### 개념 JPA 엔티티
##### 무엇
- 엔티티는 테이블 한 줄과 짝이 되는 자바 클래스다.
- @Entity를 붙이면 JPA가 이 클래스로 테이블을 만들고 조회 결과를 이 객체에 담아 준다.
##### 왜
- SQL을 직접 쓰면 ResultSet에서 칸을 하나씩 꺼내 객체에 넣는 코드가 테이블마다 반복된다.
- 엔티티를 두면 그 옮기는 일을 JPA가 한다.
##### 쉬운 방법과 비교
- JdbcTemplate으로 SQL을 쓰고 RowMapper로 옮겨도 지금 목록은 나온다.
- 9장 board-update-delete에서 필드 값만 바꾸면 UPDATE가 저절로 나가는 변경 감지를 쓰는데, 이것은 엔티티라야 된다.
##### 헷갈리는 개념
- 엔티티와 DTO는 다르다. 엔티티는 DB 모양이고 DTO는 응답 모양이다. 엔티티는 바깥으로 내보내지 않는다.
##### 이 예제에서는
- Board는 board_tb 테이블, User는 user_tb 테이블과 짝이다.

#### 개념 ManyToOne
##### 무엇
- 여러 글이 한 회원에게 속하는 관계를 객체로 적는 방법이다.
- board_tb에 user_id 외래키 칸이 생기고, 자바에서는 board.getUser()로 회원 객체를 꺼낸다.
##### 왜
- Integer userId만 두면 글쓴이 이름을 보려고 회원을 한 번 더 조회해야 한다.
- 객체로 두면 board.getUser().getUsername()처럼 이어서 꺼낼 수 있다.
##### 쉬운 방법과 비교
- private Integer userId로 두어도 테이블은 같다.
- 하지만 다음 단계 목록에서 글쓴이 이름을 함께 보여주려면 JOIN을 손으로 써야 한다. ManyToOne이면 join fetch b.user 한 마디로 된다.
##### 헷갈리는 개념
- FetchType.LAZY는 회원을 바로 가져오지 않고 실제로 쓸 때 가져온다. EAGER는 글을 가져올 때마다 회원도 늘 함께 가져온다.
- ManyToOne의 기본값은 EAGER다. 필요 없을 때도 회원을 조회하므로 LAZY로 바꾸고, 필요할 때만 join fetch로 함께 가져온다.
- OneToMany는 반대 방향이다. 10장 reply에서 글이 댓글 목록을 가질 때 쓴다.
##### 이 예제에서는
- Board의 private User user 위 @ManyToOne(fetch = FetchType.LAZY) 한 줄이다.

#### 코드
- @Table(name = "board_tb")는 테이블 이름을 정한다. user는 H2에서 예약어라서 모든 테이블 이름에 _tb를 붙인다.
- @Id는 기본키 칸이다.
- @GeneratedValue(strategy = GenerationType.IDENTITY)는 번호를 DB의 auto increment에 맡긴다.
- @Column(length = 100, nullable = false)는 칸 길이와 NOT NULL을 정한다.
- @CreationTimestamp는 INSERT 할 때 지금 시각을 넣어 준다.
- @NoArgsConstructor는 빈 생성자를 만든다. JPA가 조회 결과를 담을 객체를 만들 때 쓴다.
- @Getter는 모든 필드의 get 메서드를 만든다. setter는 만들지 않는다.

### src/main/java/com/newtenco/blog/user/User.java

#### 코드
- @Column(unique = true)는 username 칸에 UNIQUE 제약을 건다. 같은 아이디가 두 번 들어가지 못한다.
- password 길이 60은 BCrypt로 바꾼 비밀번호 길이다. 5장 user-join에서 쓴다.

## board-list T2

### src/main/resources/db/data.sql

#### 개념 data.sql
##### 무엇
- 서버가 켜질 때 실행되는 INSERT 문 파일이다.
##### 왜
- 메모리 DB는 서버를 끌 때마다 비어 버린다.
- 켤 때마다 같은 샘플 글과 회원을 넣어 두면 화면과 테스트를 바로 확인할 수 있다.
##### 쉬운 방법과 비교
- H2 콘솔에서 INSERT를 손으로 쳐도 지금은 보인다. 서버를 다시 켜면 사라진다.
- 자바 코드로 CommandLineRunner를 만들어 save 해도 된다. 하지만 SQL 몇 줄이 더 읽기 쉽다.
##### 헷갈리는 개념
- 비밀번호 칸에 1234가 아니라 $2a$10$로 시작하는 BCrypt 값을 넣었다. 6장 user-login에서 BCrypt로 비교하므로 평문을 넣으면 로그인이 실패한다. 이 값의 원래 비밀번호는 1234다.
##### 이 예제에서는
- ssar이 글 3개, cos가 글 2개를 가진다.

### src/main/resources/application.properties

#### 코드
- spring.sql.init.data-locations=classpath:db/data.sql은 실행할 SQL 파일 위치다. classpath는 resources 폴더다.
- spring.jpa.defer-datasource-initialization=true는 JPA가 테이블을 만든 뒤에 data.sql을 실행하게 한다. 없으면 테이블이 없어서 INSERT가 실패한다.

## board-list T3

### src/main/java/com/newtenco/blog/board/BoardRepository.java

#### 개념 Repository
##### 무엇
- DB에 묻는 일을 맡는 인터페이스다.
- JpaRepository를 상속하면 save, findById, findAll, delete 같은 메서드를 구현 없이 쓴다.
##### 왜
- DB 코드를 한 곳에 모으면 서비스는 무엇을 할지만 쓴다.
- 인터페이스만 만들면 스프링이 구현 객체를 만들어 주입한다.
##### 쉬운 방법과 비교
- 상속받은 findAll()을 그대로 써도 목록은 나온다.
- 그런데 글마다 board.getUser()를 꺼낼 때 회원 SELECT가 따로 나간다. 글이 많아지면 쿼리 수가 글 수만큼 는다. 그래서 join fetch를 쓴 쿼리를 따로 만든다.
##### 헷갈리는 개념
- @Query 안은 SQL이 아니라 JPQL이다. 테이블 board_tb가 아니라 엔티티 이름 Board와 필드 이름 b.user를 쓴다.
- join과 join fetch는 다르다. join fetch는 회원 객체까지 한 번에 채워 온다.
##### 이 예제에서는
- JpaRepository<Board, Integer>는 Board 엔티티를 다루고 기본키가 Integer라는 뜻이다.

#### 코드
- @Query는 메서드에 직접 JPQL을 붙인다.
- join fetch b.user는 글과 글쓴이를 한 번의 SELECT로 가져온다.
- order by b.id desc는 최신 글이 위에 오게 한다.

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 개념 응답 DTO
##### 무엇
- DTO(Data Transfer Object)는 화면에 줄 값만 담은 객체다.
- 엔티티에서 필요한 칸만 골라 담는다.
##### 왜
- 엔티티를 그대로 돌려주면 회원의 비밀번호까지 JSON에 나간다.
- join fetch 없이 지연 로딩한 user는 진짜 객체가 아니라 대리 객체라 JSON으로 바꾸다 오류가 난다.
##### 쉬운 방법과 비교
- List<Board>를 그대로 돌려주는 방법이 먼저 떠오른다. 지금은 join fetch 덕에 응답은 나오지만 user 안의 password가 그대로 보인다.
- 10장 reply에서 글이 댓글 목록을 가지고 댓글이 다시 글을 가지면 JSON이 끝없이 이어진다. DTO는 이 일이 없다.
##### 헷갈리는 개념
- 요청 DTO와 응답 DTO는 따로 둔다. 요청은 5장 user-join부터 Request 클래스에 만든다.
##### 이 예제에서는
- ListDTO는 글 번호, 제목, 내용, 글쓴이 이름, 쓴 시각만 담는다.

#### 코드
- public class BoardResponse 안에 record를 넣어 글 응답 DTO를 한 파일에 모은다. 쓸 때는 BoardResponse.ListDTO로 부른다.
- record는 필드, 생성자, get 메서드, equals를 자동으로 만든다. 값만 담는 DTO에 알맞다.
- record의 get 메서드 이름은 id(), title()이다. JSON 칸 이름도 id, title이 된다.
- public ListDTO(Board board)는 엔티티를 받는 생성자를 하나 더 만든 것이다.
- this(...)는 record의 기본 생성자를 불러 칸을 채운다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- @Service는 스프링이 이 클래스를 객체로 만들어 관리하게 한다.
- @RequiredArgsConstructor는 final 필드를 받는 생성자를 만든다. 스프링이 이 생성자로 BoardRepository를 넣어 준다.
- @Transactional(readOnly = true)는 메서드를 하나의 트랜잭션에서 실행한다. 읽기만 하므로 readOnly로 둔다. 저장은 8장 board-write에서 다룬다.
- 글목록처럼 메서드 이름을 한글로 써도 자바는 컴파일된다. 서비스 메서드는 하는 일을 한글로 읽히게 쓴다.
- stream().map(BoardResponse.ListDTO::new)는 Board마다 ListDTO 생성자를 불러 바꾼다.
- toList()는 바꾼 결과를 List로 모은다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @RestController는 돌려준 객체를 JSON으로 바꿔 응답한다.
- private final BoardService boardService와 @RequiredArgsConstructor로 서비스를 주입받는다.
- 컨트롤러는 주소만 받고 일은 서비스에 넘긴다.

## board-list T4

### src/main/java/com/newtenco/blog/core/util/Resp.java

#### 개념 공통 응답 Resp
##### 무엇
- 모든 응답을 status, msg, body 세 칸으로 감싸는 record다.
- 성공이면 body에 데이터가 들고, 실패면 msg에 이유가 든다.
##### 왜
- 응답마다 모양이 다르면 리액트가 주소마다 다르게 읽어야 한다.
- 모양이 하나면 리액트는 늘 msg와 body만 보면 된다.
##### 쉬운 방법과 비교
- DTO를 그대로 돌려줘도 지금 목록은 잘 나온다.
- 3장 board-detail에서 없는 글을 404로 알릴 때 실패에도 같은 모양이 필요하다. 7장 jwt-auth의 401 응답도 RespFilter로 같은 모양을 만든다.
##### 헷갈리는 개념
- ResponseEntity는 HTTP 상태 코드와 몸통을 담는 스프링 클래스다. Resp는 그 몸통 안 JSON 모양이다. Resp의 status 칸과 HTTP 상태 코드는 같은 값을 넣는다.
##### 이 예제에서는
- 목록 응답이 body 칸 안에 글 배열이 든 모양으로 바뀐다.

#### 코드
- Resp<T>의 T는 body에 무엇이든 담기 위한 타입 자리다.
- public static <B> ResponseEntity<Resp<B>> ok(B body)는 static 메서드에서 쓸 타입 자리 B를 따로 연다.
- ResponseEntity.ok(...)는 HTTP 200으로 응답한다.
- new Resp<>(200, "성공", body)의 <>는 타입을 앞에서 알아서 맞춘다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- 돌려주는 타입이 ResponseEntity<?>로 바뀐다. ?는 몸통 타입을 따지지 않는다는 뜻이다. 성공과 실패의 몸통 타입이 달라도 한 타입으로 쓸 수 있다.
- Resp.ok(boardService.글목록())으로 서비스 결과를 감싼다.

## board-detail T1

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @GetMapping("/api/boards/{id}")와 @PathVariable("id")로 주소의 글 번호를 받는다.

### src/main/java/com/newtenco/blog/board/BoardRepository.java

#### 코드
- where b.id = :id에서 :id는 JPQL의 이름 붙은 자리다.
- @Param("id")는 메서드 인자를 :id 자리에 넣는다.
- Optional<Board>는 글이 없을 수도 있다는 뜻이다. null 대신 빈 Optional이 온다.
- 상속받은 findById를 쓰지 않는 이유는 글쓴이도 한 번에 가져오려고 join fetch를 붙이기 위해서다.

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 코드
- DetailDTO는 상세 화면에 맞춘 DTO다. 목록과 달리 userId를 담는다. 11장 board-owner에서 내 글인지 가릴 때 쓴다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- orElseThrow()는 Optional에 글이 있으면 꺼내고 없으면 예외를 던진다.
- 지금은 없는 글이면 NoSuchElementException이 나서 500 오류가 된다. 다음 단계에서 404로 바꾼다.

## board-detail T2

### src/main/java/com/newtenco/blog/core/handler/errors/NotFoundException.java

#### 개념 사용자 예외
##### 무엇
- 우리가 만든 예외 클래스다. 이름만 봐도 어떤 실패인지 알 수 있다.
- RuntimeException을 상속하면 throws를 적지 않아도 던질 수 있다.
##### 왜
- 없는 글을 찾으면 NoSuchElementException이 나서 500이 된다. 서버 잘못이 아닌데 서버 오류로 보인다.
- 이 실패는 404라고 알려야 리액트가 찾을 수 없음을 보여준다.
##### 쉬운 방법과 비교
- 서비스에서 null을 돌려주고 컨트롤러에서 if로 검사해 404를 만들어도 지금은 된다.
- 9장 board-update-delete에서 수정, 수정 정보, 삭제가 모두 같은 검사를 한다. if를 메서드마다 쓰는 대신 예외를 던지면 한 곳에서 응답을 만든다.
- 스프링의 ResponseStatusException(HttpStatus.NOT_FOUND)을 던져도 된다. 하지만 이 장 T4가 끝나면 메시지가 Not Found로 바뀌어 게시글을 찾을 수 없습니다가 사라진다.
##### 헷갈리는 개념
- Exception을 상속하면 체크 예외라서 부르는 메서드마다 throws를 적어야 한다.
##### 이 예제에서는
- 5장부터 BadRequestException, 7장에 UnauthorizedException, 9장에 ForbiddenException이 같은 모양으로 더해진다.

#### 코드
- super(message)는 메시지를 RuntimeException에 넘긴다. e.getMessage()로 다시 꺼낸다.

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 개념 전역 예외 처리 404
##### 무엇
- 어느 컨트롤러에서 예외가 나도 이 클래스가 받아서 응답을 만든다.
- @RestControllerAdvice가 모든 컨트롤러에 적용하고, @ExceptionHandler가 어떤 예외를 받을지 정한다.
##### 왜
- 예외를 그대로 두면 스프링 기본 오류 JSON이 나간다. timestamp, error, path 칸이 있어서 Resp 모양과 다르다.
- 여기서 받으면 실패도 status, msg, body 모양이 된다.
##### 쉬운 방법과 비교
- 컨트롤러마다 try catch로 감싸도 된다.
- 9장 board-update-delete부터 메서드가 늘면 같은 catch가 모든 메서드에 반복된다. 이 클래스 하나면 예외를 던지기만 하면 된다.
##### 헷갈리는 개념
- @ControllerAdvice는 화면 이름을 돌려줄 때 쓰고, @RestControllerAdvice는 JSON을 돌려줄 때 쓴다.
- 필터에서 난 예외는 여기까지 오지 않는다. 그래서 7장 jwt-auth의 401은 RespFilter로 따로 쓴다.
##### 이 예제에서는
- NotFoundException이 오면 HTTP 404와 msg 게시글을 찾을 수 없습니다를 돌려준다.

#### 코드
- 메서드 인자 NotFoundException e로 던진 예외 객체를 받는다.
- e.getMessage()로 서비스에서 넣은 문구를 꺼내 msg에 담는다.

### src/main/java/com/newtenco/blog/core/util/Resp.java

#### 코드
- fail은 실패 응답을 만든다. body가 없으므로 타입을 Resp<Void>로 두고 null을 넣는다.
- ResponseEntity.status(status).body(...)는 200이 아닌 상태 코드로 응답한다.
- status.value()는 HttpStatus.NOT_FOUND에서 숫자 404를 꺼낸다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- orElseThrow(() -> new NotFoundException(...))는 글이 없을 때 던질 예외를 람다로 넘긴다. 글이 없을 때만 예외 객체를 만든다.

## board-detail T3

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- @ExceptionHandler(Exception.class)는 위에서 받지 못한 나머지 예외를 모두 받는다. 더 구체적인 NotFoundException은 여전히 ex404가 받는다.
- 응답 msg는 관리자에게 문의하세요로 둔다. 오류 내용을 그대로 내보내면 SQL이나 클래스 이름이 밖에 보인다.
- @Slf4j는 log 필드를 만들어 준다.
- log.error("예상하지 못한 오류", e)는 응답에서 숨긴 오류 내용을 서버 콘솔에 남긴다. 고칠 사람은 여기서 원인을 본다.

## board-detail T4

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- ErrorResponse는 스프링이 상태 코드를 알고 던지는 예외들이 구현하는 인터페이스다. 없는 주소의 404, 허용하지 않은 메서드의 405가 그 예다.
- 바로 앞 단계 코드는 이 예외까지 모두 500으로 바꿔 버렸다. 그래서 먼저 걸러 원래 상태를 지킨다.
- e instanceof ErrorResponse er은 검사와 형 변환을 한 번에 한다. 맞으면 er로 바로 쓴다.
- er.getStatusCode().value()로 숫자를 꺼내고 HttpStatus.valueOf로 다시 HttpStatus를 만든다.
- status.getReasonPhrase()는 Not Found 같은 상태 이름이다. msg에 넣는다.
- 5장 user-join T6에서 빠진 요청 값은 이 문구 대신 따로 메시지를 주도록 핸들러를 더한다.

## board-paging T1

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @RequestParam(value = "page", defaultValue = "0")은 주소의 ?page= 값을 받는다. 없으면 0쪽이다.
- 쪽 번호는 0부터 센다. Spring Data의 쪽 번호가 0부터라서 그대로 맞춘다.

### src/main/java/com/newtenco/blog/board/BoardRepository.java

#### 개념 Pageable
##### 무엇
- 몇 쪽을, 한 쪽에 몇 개씩, 어떤 순서로 가져올지 담은 요청 정보다.
- 쿼리 메서드 인자에 넣으면 Spring Data가 SQL 끝에 order by와 limit, offset을 붙인다.
##### 왜
- 글이 늘면 한 번에 모두 보내기 무겁다.
- 한 쪽씩 끊어 가져오면 DB도 그 쪽만 읽는다.
##### 쉬운 방법과 비교
- 모두 가져와서 자바에서 subList로 자르면 지금 글 5개로는 잘 된다. 하지만 글이 만 개면 만 개를 읽고 세 개만 쓴다.
- JPQL에 limit을 손으로 쓸 수 없고, 네이티브 SQL로 쓰면 offset 계산을 직접 해야 한다.
##### 헷갈리는 개념
- Pageable은 요청이고 Page는 결과다.
- PageRequest는 Pageable을 만드는 클래스다. 서비스에서 PageRequest.of로 만든다.
##### 이 예제에서는
- 서비스에서 3개씩, id 내림차순으로 만든 Pageable을 넘긴다.

#### 개념 Page
##### 무엇
- 한 쪽의 글 목록과 함께 전체 쪽 수, 첫 쪽인지, 끝 쪽인지를 담은 결과다.
- 전체 쪽 수를 알려면 전체 글 수가 필요해서 count 쿼리가 한 번 더 나간다.
##### 쉬운 방법과 비교
- List<Board>로 받아도 쪽 나누기는 된다. 다만 다음 쪽이 있는지 몰라서 리액트가 다음 버튼을 언제 막을지 모른다. 다음 단계에서 이 정보를 응답에 담는다.
##### 헷갈리는 개념
- Slice는 다음 쪽이 있는지만 알고 count 쿼리를 하지 않는다. 이 예제는 전체 쪽 수가 필요해서 Page를 쓴다.
##### 이 예제에서는
- findAllJoinUser가 List 대신 Page<Board>를 돌려준다.

#### 코드
- countQuery는 전체 글 수를 세는 쿼리를 따로 적는다. join fetch가 든 쿼리로는 Spring Data가 count 쿼리를 만들지 못해서 오류가 난다.
- order by를 쿼리에서 지웠다. 정렬은 Pageable의 Sort가 붙인다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- PageRequest.of(page, 3, ...)는 page쪽을 3개씩 가져오라는 Pageable이다. 한 쪽 글 수는 3으로 고정한다.
- Sort.by(Sort.Direction.DESC, "id")는 id 큰 순서, 즉 최신 글부터다. "id"는 칸 이름이 아니라 엔티티 필드 이름이다.
- getContent()는 Page에서 이번 쪽 글 목록만 꺼낸다.

## board-paging T2

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 코드
- PageDTO는 Page를 응답 모양으로 옮긴 DTO다. Page를 그대로 내보내면 pageable, sort 같은 쓰지 않는 칸까지 나간다.
- boards는 이번 쪽 글을 ListDTO로 바꾼 목록이다.
- getNumber()는 지금 쪽 번호, getTotalPages()는 전체 쪽 수다.
- isFirst()와 isLast()는 리액트가 이전 버튼과 다음 버튼을 막을 때 쓴다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- 서비스는 Page를 PageDTO 생성자에 넘기기만 한다. ListDTO로 바꾸는 일은 PageDTO 안으로 옮겼다.

## board-paging T3

### src/main/java/com/newtenco/blog/board/BoardRepository.java

#### 개념 제목 검색 쿼리
##### 무엇
- 제목에 검색어가 들어간 글만 가져오는 JPQL이다.
- like '%검색어%'는 앞뒤에 무엇이 있어도 검색어를 포함하면 맞는다.
##### 왜
- 글이 많아지면 쪽을 넘기며 찾기 어렵다.
##### 쉬운 방법과 비교
- 메서드 이름만으로 findByTitleContaining(String keyword, Pageable pageable)를 만들어도 검색과 쪽 나누기는 된다.
- 하지만 이름 규칙 쿼리에는 join fetch를 붙일 수 없다. 글마다 회원 SELECT가 따로 나간다. 그래서 앞 단계 쿼리에 where만 더한다.
- 검색할 때와 안 할 때 메서드를 둘로 나눌 수도 있다. 빈 검색어는 like '%%'가 되어 모든 글과 맞으므로 메서드 하나로 된다.
##### 헷갈리는 개념
- 같은 조건을 countQuery에도 넣어야 한다. 빠뜨리면 목록은 검색 결과인데 전체 쪽 수는 모든 글 기준이 된다.
##### 이 예제에서는
- /api/boards?keyword=제목1이면 제목에 제목1이 든 글만 나온다.

#### 코드
- concat('%', :keyword, '%')는 JPQL에서 검색어 앞뒤에 %를 붙인다. 문자열을 더해 쿼리를 만들지 않으므로 검색어에 따옴표가 들어와도 안전하다.
- @Param("keyword")로 검색어를 :keyword 자리에 넣는다. Pageable은 @Param 없이 Spring Data가 알아서 쓴다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @RequestParam(value = "keyword", defaultValue = "")은 검색어가 없으면 빈 글자로 받는다. 그러면 모든 글이 나온다.

## user-join T1

### build.gradle

#### 코드
- spring-boot-starter-security는 Spring Security를 넣는다.
- 이 한 줄만 넣고 서버를 켜면 모든 주소가 잠긴다.
- 글 목록을 불러도 401이 오고, 콘솔에 자동으로 만든 비밀번호가 찍힌다.
- Security는 넣는 순간 안전한 쪽을 기본으로 고른다. 그래서 무엇을 열지는 우리가 설정 파일로 정해야 한다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 Security 기본 설정
##### 무엇
- Spring Security는 요청이 컨트롤러에 닿기 전에 지나가는 필터 묶음이다.
- 이 묶음을 필터 체인(SecurityFilterChain)이라고 부른다.
- 우리는 SecurityFilterChain을 빈으로 만들어 어떤 기능을 켜고 끌지 정한다.
##### 왜
- starter만 넣으면 모든 주소가 로그인 화면이나 401로 막힌다.
- 앞 장에서 만든 글 목록, 글 상세 API가 한꺼번에 멈춘다.
- 그래서 이 단계에서는 먼저 모든 주소를 열어 둔다. 앞 장 테스트가 그대로 통과한다.
##### 쉬운 방법과 비교
- Security를 넣지 않고 컨트롤러마다 if로 로그인을 검사할 수도 있다.
- 회원가입만 있는 지금은 그래도 된다.
- 7장 jwt-auth에서 주소마다 로그인 검사가 필요해지면 컨트롤러마다 같은 if가 반복된다.
- 13장 admin-api에서 관리자 주소까지 생기면 주소 규칙 한 곳에서 막는 쪽이 훨씬 짧다.
##### 헷갈리는 개념
- 필터는 컨트롤러보다 먼저 돈다. 인터셉터나 컨트롤러 안 검사와 다른 층이다.
- permitAll은 Security 검사만 통과시킨다. 없는 주소면 그대로 404가 난다.
##### 이 예제에서는
- filterChain 메서드 하나에 설정을 위에서 아래로 쌓는다.
- 뒤 장에서도 이 메서드에 줄을 더해 간다.

#### 코드
- @Configuration과 @Bean은 이 메서드가 돌려준 SecurityFilterChain을 스프링이 쓰게 한다.
- headers의 frameOptions sameOrigin은 같은 서버 화면 안의 iframe을 허락한다. H2 콘솔이 iframe으로 그려지기 때문이다.
- csrf disable은 CSRF 검사를 끈다. 이 서버는 쿠키 세션이 아니라 토큰을 헤더로 받을 것이라 CSRF가 노리는 틈이 없다.
- formLogin disable은 Security가 만들어 주는 로그인 화면을 끈다. 우리는 JSON으로 로그인할 것이다.
- httpBasic disable은 브라우저 팝업으로 아이디를 묻는 방식을 끈다.
- anyRequest().permitAll()은 모든 주소를 연다. 7장 jwt-auth에서 주소별 규칙으로 바꾼다.
- http.build()가 지금까지 쌓은 설정으로 필터 체인을 만든다.

## user-join T2

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 BCrypt
##### 무엇
- BCrypt는 비밀번호를 되돌릴 수 없는 글자로 바꾸는 해시 방식이다.
- 한 번 바꾼 값을 다시 원래 비밀번호로 풀 방법이 없다. 이것을 단방향이라고 한다.
- 바꿀 때마다 솔트(salt)라는 무작위 값을 섞는다. 그래서 같은 1234도 바꿀 때마다 결과가 다르다.
- 결과 글자 안에 솔트와 반복 횟수가 함께 들어 있다. 길이는 항상 60자다.
##### 왜
- 비밀번호를 그대로 저장하면 DB가 새는 순간 모든 회원의 비밀번호가 드러난다.
- 같은 비밀번호를 다른 사이트에도 쓰는 회원이 많아서 피해가 이 서비스 밖으로 번진다.
##### 쉬운 방법과 비교
- 비밀번호를 그대로 저장하고 로그인 때 equals로 비교할 수도 있다.
- 기능만 보면 지금도, 6장 user-login에서도 잘 돈다.
- 차이는 DB가 새는 날에만 보인다. 그래서 처음부터 해시로 저장한다.
- SHA-256처럼 솔트 없는 해시를 쓰면 같은 비밀번호가 같은 값이 된다. 미리 계산한 표로 거꾸로 찾을 수 있다.
##### 헷갈리는 개념
- 암호화와 다르다. 암호화는 열쇠가 있으면 다시 풀 수 있고, 해시는 풀 수 없다.
- 그래서 로그인 때는 풀어서 비교하지 않는다. 6장 user-login에서 matches로 다시 해시해서 비교한다.
##### 이 예제에서는
- passwordEncoder 빈이 BCryptPasswordEncoder를 돌려준다.
- UserService가 회원가입 때 encode로 바꾼 값을 저장한다.
- data.sql의 샘플 회원 비밀번호도 이미 $2a$10$로 시작하는 BCrypt 값이다.

#### 코드
- 반환 타입을 PasswordEncoder 인터페이스로 쓴다. 쓰는 쪽은 BCrypt인지 몰라도 된다.
- new BCryptPasswordEncoder()는 기본 반복 횟수 10으로 만든다. 해시 값의 $10$이 그 뜻이다.

### src/main/java/com/newtenco/blog/user/User.java

#### 코드
- @Builder를 생성자에 붙인다. 이름을 붙여 원하는 칸만 채워 만들 수 있다.
- 클래스가 아니라 생성자에 붙이면 빌더가 쓸 칸을 생성자 매개변수로 직접 고른다.
- id와 createdAt도 넣어 둔다. 7장 jwt-auth에서 토큰의 id로 User를 만들 때 쓴다.

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 코드
- JoinDTO는 회원가입 요청 JSON을 받는 record다.
- toEntity가 encPassword를 따로 받는다. 요청의 원래 비밀번호가 엔티티로 그대로 들어가는 길을 막는다.

### src/main/java/com/newtenco/blog/user/UserResponse.java

#### 코드
- 응답 DTO에 password를 넣지 않는다. 해시 값이라도 밖으로 내보내지 않는다.
- DTO(User user) 생성자는 엔티티를 받아 record의 기본 생성자로 넘긴다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 생성자 주입으로 PasswordEncoder를 받는다. SecurityConfig에서 만든 빈이 들어온다.
- passwordEncoder.encode는 원래 비밀번호를 BCrypt 값으로 바꾼다.
- 메서드의 @Transactional은 클래스의 readOnly = true를 덮어쓴다. 이 메서드만 쓰기 트랜잭션이 된다.
- 조회 메서드는 클래스 설정대로 readOnly로 두고, 저장하는 메서드에만 @Transactional을 붙인다.
- save가 돌려준 savedUser에는 DB가 만든 id가 채워져 있다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- POST /join은 /api 아래가 아니다. 리액트 따라치기가 부르는 주소를 그대로 따른다.

## user-join T3

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 개념 Bean Validation
##### 무엇
- Bean Validation은 필드에 어노테이션으로 규칙을 적고, 스프링이 그 규칙으로 값을 검사하게 하는 방법이다.
- 규칙을 어기면 컨트롤러 메서드가 실행되기 전에 예외가 난다.
##### 왜
- 앞 단계에서는 빈 아이디나 엉터리 이메일도 그대로 저장된다.
- username 칸이 null이면 DB 제약에 걸려 500이 난다. 학생 화면에는 무엇이 틀렸는지 안 보인다.
##### 쉬운 방법과 비교
- 서비스에서 if (username == null || username.isBlank()) 처럼 직접 검사할 수도 있다.
- 회원가입 하나뿐인 지금은 그래도 된다.
- 6장 user-login, 8장 board-write, 12장 user-update에서 같은 검사가 계속 늘어난다.
- 어노테이션이면 규칙이 DTO 칸 바로 옆에 있어서 읽기 쉽고, 오류 응답은 한 곳에서 만든다.
- 아이디 중복처럼 DB를 봐야 하는 검사는 어노테이션으로 못 한다. 그건 다음 단계처럼 서비스에서 한다.
##### 헷갈리는 개념
- @NotNull은 null만 막는다. @NotBlank는 null, 빈 글자, 공백만 있는 글자까지 막는다.
- @Size는 글자 수 규칙이다. 숫자 크기 규칙은 @Min, @Max다.
- DTO의 @Size와 엔티티의 @Column(length)은 다르다. 앞은 요청을 거르고, 뒤는 테이블을 만든다.
##### 이 예제에서는
- JoinDTO의 세 칸에 규칙을 붙이고, 컨트롤러 매개변수에 @Valid를 붙인다.
- 틀리면 GlobalExceptionHandler가 첫 번째 메시지를 400으로 돌려준다.

#### 코드
- record 매개변수 앞에 바로 어노테이션을 붙인다. 칸 하나에 여러 규칙을 쌓을 수 있다.
- message는 규칙을 어겼을 때 돌려줄 문구다. 화면에 그대로 보여 줄 한국어로 쓴다.
- @Email은 형식만 본다. 실제로 있는 메일 주소인지는 모른다.

### build.gradle

#### 코드
- spring-boot-starter-validation이 검사기를 넣는다. 이것이 없으면 어노테이션을 붙여도 아무 일도 일어나지 않는다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- @Valid가 붙은 매개변수만 검사한다. 빠뜨리면 오류 없이 검사 없이 지나간다.

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- MethodArgumentNotValidException은 @Valid 검사에 실패하면 스프링이 던지는 예외다.
- getBindingResult().getFieldErrors()는 틀린 칸 목록이다.
- getFirst()로 첫 번째만 쓴다. 화면에는 한 번에 한 문구만 보여 준다.
- getDefaultMessage()가 어노테이션에 적은 message 값이다.

## user-join T4

### src/main/java/com/newtenco/blog/user/UserRepository.java

#### 개념 아이디 중복 확인
##### 무엇
- 저장하기 전에 같은 username이 이미 있는지 DB에 물어보는 것이다.
- 있으면 저장하지 않고 400으로 이유를 알린다.
##### 왜
- User의 username 칸에는 unique 제약이 있다. 같은 아이디를 넣으면 DB가 거절한다.
- 그런데 그 거절은 DB 예외라서 500과 알아보기 힘든 문구로 나간다.
##### 쉬운 방법과 비교
- DB unique 제약에만 맡길 수도 있다. 막히기는 한다.
- 하지만 무엇이 틀렸는지 회원이 알 수 없다.
- 그래서 먼저 물어보고 사람이 읽을 문구로 알린다.
- 두 사람이 같은 순간에 같은 아이디로 가입하면 둘 다 확인을 통과할 수 있다. 그때 마지막으로 막는 것이 unique 제약이다. 둘은 함께 둔다.
##### 헷갈리는 개념
- existsBy는 개수만 본다. 회원 전체를 꺼내는 findBy보다 가볍다.
- 다음 단계의 중복 확인 API는 화면이 미리 보여 주려는 것이다. 회원가입의 검사는 API를 바로 부르는 사람까지 막으려는 것이다. 둘 다 필요하다.
##### 이 예제에서는
- UserRepository의 existsByUsername을 UserService.회원가입 맨 위에서 부른다.

#### 코드
- existsByUsername은 메서드 이름만 쓰면 Spring Data JPA가 쿼리를 만든다. 반환 타입이 boolean이다.

### src/main/java/com/newtenco/blog/core/handler/errors/BadRequestException.java

#### 코드
- NotFoundException과 같은 모양의 사용자 예외다. 400으로 알릴 때 던진다.

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- ex400은 BadRequestException의 메시지를 400으로 감싼다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 검사를 encode보다 먼저 둔다. 막힐 요청에 느린 BCrypt를 돌리지 않는다.

## user-join T5

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- GET /check-username?username=ssar 모양으로 부른다.
- @RequestParam("username")은 주소 뒤 물음표 값을 꺼낸다. 이름을 괄호에 적어 두면 컴파일 옵션과 상관없이 찾는다.

### src/main/java/com/newtenco/blog/user/UserResponse.java

#### 코드
- AvailableDTO는 boolean 하나를 감싼다. 응답 body가 true가 아니라 {"available": true}가 되어 뜻이 이름으로 보인다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 있으면 쓸 수 없으니 existsByUsername 결과를 느낌표로 뒤집어 available로 쓴다.
- @Transactional을 붙이지 않는다. 조회라서 클래스의 readOnly를 그대로 쓴다.

## user-join T6

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- MissingServletRequestParameterException은 @RequestParam 값이 빠졌을 때 스프링이 던진다.
- 앞에서는 스프링 기본 문구가 나갔다. 다른 400 응답과 같은 모양의 한국어 문구로 맞춘다.
- getParameterName()은 빠진 값의 이름이다. 예를 들어 username:값을 입력해주세요가 된다.

## user-login T1

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- findByUsername으로 회원을 찾고, 없으면 401을 던진다.
- passwordEncoder.matches는 입력한 비밀번호를 저장된 값 안의 솔트로 다시 해시해서 비교한다.
- 저장된 값을 풀어서 비교하는 것이 아니다. BCrypt는 풀 수 없다.
- 아이디가 없을 때와 비밀번호가 틀릴 때 같은 문구를 쓴다.
- 문구가 다르면 어떤 아이디가 가입되어 있는지 로그인 화면으로 알아낼 수 있다. 그래서 둘을 구분해 주지 않는다.
- 로그인은 조회만 하므로 @Transactional을 붙이지 않는다.

### src/main/java/com/newtenco/blog/user/UserRepository.java

#### 코드
- findByUsername은 없을 수도 있어서 Optional로 받는다.

### src/main/java/com/newtenco/blog/core/handler/errors/UnauthorizedException.java

#### 코드
- 로그인에 실패했다는 뜻의 401 사용자 예외다.

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- ex401은 UnauthorizedException을 401로 감싼다.

## user-login T2

### src/main/java/com/newtenco/blog/core/util/JwtUtil.java

#### 개념 JWT 만들기
##### 무엇
- JWT(JSON Web Token)는 누가 로그인했는지를 담은 글자 한 줄이다.
- 점으로 나뉜 세 조각이다. 머리, 내용, 서명이다.
- 내용에는 id, username, 만료 시각 같은 값을 담는다. 이 값 하나하나를 클레임이라고 부른다.
- 서명은 서버만 아는 비밀 키로 만든다. 내용을 한 글자라도 바꾸면 서명이 맞지 않는다.
##### 왜
- 로그인은 한 번 하고, 그 뒤 요청마다 내가 누구인지 알려야 한다.
- 매번 아이디와 비밀번호를 보내는 것은 위험하다. 대신 로그인 때 받은 토큰을 보낸다.
##### 쉬운 방법과 비교
- 학생이 먼저 떠올리는 방법은 세션 로그인이다. 서버가 세션에 회원을 기억하고 브라우저는 쿠키만 들고 다닌다.
- 지금도 세션으로 충분히 된다.
- 이 강의의 손님은 다른 주소에서 도는 리액트 앱이다. 쿠키보다 헤더로 토큰을 주고받는 쪽이 단순하다.
- 7장 jwt-auth에서 서버는 아무것도 기억하지 않고 토큰만 보고 회원을 꺼낸다.
- 대신 토큰은 한 번 주면 거둘 수 없다. 14장 admin-roles에서 역할을 바꿔도 이미 받은 토큰에는 옛 역할이 남는다.
- 15장 refresh-token의 로그아웃도 이미 받은 액세스 토큰은 막지 못한다. 세션이었다면 지우기만 하면 된다.
- 같은 서버가 화면까지 그리는 사이트라면 세션이 더 쉽다.
##### 헷갈리는 개념
- JWT의 내용은 암호화가 아니다. 누구나 풀어서 읽을 수 있다. 그래서 비밀번호는 넣지 않는다.
- 서명은 숨기는 것이 아니라 바꾸지 못하게 하는 것이다.
- Bearer는 이 토큰을 가진 사람이 주인이라는 뜻의 이름표다. 토큰 앞에 붙이는 관례다.
##### 이 예제에서는
- JwtUtil.create가 회원으로 토큰을 만들고 앞에 Bearer 를 붙인다.
- UserService.로그인이 토큰을 만들고, UserController.login이 응답 헤더에 담는다.

#### 코드
- 상수 HEADER와 TOKEN_PREFIX는 7장 jwt-auth의 필터도 같이 쓴다. 글자를 한 곳에서만 쓴다.
- EXPIRATION_TIME은 밀리초다. 1000L * 60 * 60 * 24는 하루다. L을 붙여 long으로 곱한다.
- SECRET은 서명용 비밀 키다. HMAC512는 64바이트 이상을 권하므로 길게 쓴다.
- 강의라서 코드에 적었다. 실제 서비스는 키를 코드 밖 환경 값으로 둔다.
- Algorithm.HMAC512는 비밀 키 하나로 서명하고 검증하는 방식이다.
- 모든 메서드가 static이다. 상태가 없는 도구라서 빈으로 만들지 않고 바로 부른다.
- withSubject는 토큰의 주인이다. username을 넣는다.
- withExpiresAt은 만료 시각이다. 지나면 검증에서 실패한다.
- withClaim으로 id와 username을 더 담는다. 7장 jwt-auth에서 DB를 보지 않고 꺼낸다.
- sign이 서명까지 붙여 완성된 글자를 만든다.

### build.gradle

#### 코드
- com.auth0의 java-jwt는 JWT를 만들고 검증하는 라이브러리다. 버전을 직접 적는다. 스프링이 관리하는 라이브러리가 아니기 때문이다.

### src/main/java/com/newtenco/blog/user/UserResponse.java

#### 코드
- LoginDTO는 서비스가 컨트롤러로 토큰과 회원을 함께 넘기는 그릇이다.
- 응답 JSON으로 그대로 나가지 않는다. 토큰은 헤더로, 회원은 body로 나눠 담는다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 개념 응답 헤더 Authorization
##### 무엇
- 응답의 Authorization 헤더에 Bearer 토큰을 담아 돌려준다.
- 리액트는 이 헤더를 꺼내 저장하고, 다음 요청의 Authorization 헤더에 그대로 붙인다.
##### 쉬운 방법과 비교
- body에 accessToken 칸으로 넣어 주는 방법도 있다. 그것도 된다.
- 이 강의는 리액트 따라치기가 헤더에서 읽으므로 그 약속을 따른다.
- 헤더로 주면 다음 단계의 CORS 노출 설정이 꼭 필요하다.
##### 이 예제에서는
- login이 ResponseEntity.ok().header(...)로 헤더를 붙인다.

#### 코드
- Resp.ok는 헤더를 붙일 자리가 없다. 그래서 ResponseEntity.ok()로 직접 만든다.
- header(JwtUtil.HEADER, ...)가 Authorization 헤더를 단다.
- body에 new Resp<>(200, "성공", ...)를 넣어 다른 응답과 같은 모양을 지킨다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 로그인의 반환 타입을 LoginDTO로 바꾼다. 비밀번호가 맞은 다음에만 JwtUtil.create를 부른다.

## user-login T3

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 코드
- 로그인에는 @NotBlank만 붙인다. 글자 수 규칙은 가입 때 이미 검사했다.
- 검사가 없으면 빈 아이디도 DB까지 가서 아이디나 비밀번호가 틀렸다는 401이 난다. 이제는 무엇을 빠뜨렸는지 400으로 알린다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- login의 매개변수에도 @Valid를 붙인다. 붙이지 않으면 위 규칙이 돌지 않는다.

## user-login T4

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 CORS 노출 헤더
##### 무엇
- CORS는 다른 주소에서 온 화면이 이 서버를 불러도 되는지 브라우저가 묻고 서버가 답하는 약속이다.
- 리액트는 localhost:5173, 서버는 localhost:8080이라 주소가 다르다.
- 노출 헤더(exposed headers)는 그 화면의 자바스크립트가 응답에서 읽어도 되는 헤더 목록이다.
##### 왜
- 브라우저는 다른 주소의 응답에서 Content-Type 같은 몇 가지 기본 헤더만 자바스크립트에 보여 준다.
- Authorization은 그 목록에 없다. 서버가 보냈어도 리액트에서 읽으면 null이 나온다.
- Postman이나 개발자 도구 네트워크 탭에는 헤더가 보인다. 그래서 왜 못 읽는지 헷갈린다.
##### 쉬운 방법과 비교
- 컨트롤러에 @CrossOrigin을 붙이는 방법도 있다.
- Security 필터는 컨트롤러보다 먼저 돈다. 7장 jwt-auth에서 401로 막히는 응답에는 컨트롤러의 CORS 설정이 붙지 않는다.
- 그래서 Security 필터 체인에 CORS를 건다.
##### 헷갈리는 개념
- allowedHeaders는 요청에 실어 보내도 되는 헤더다. exposedHeaders는 응답에서 읽어도 되는 헤더다. 방향이 반대다.
- CORS는 브라우저만 지키는 규칙이다. 서버 사이 호출이나 curl에는 상관없다.
##### 이 예제에서는
- corsConfigurationSource 빈의 setExposedHeaders(List.of("Authorization")) 한 줄이 핵심이다.
- http.cors(...)로 이 설정을 필터 체인에 건다.

#### 코드
- @Value("${app.cors.allowed-origins}")는 application.properties의 값을 필드에 넣는다.
- split(",")로 쉼표로 이은 주소 여러 개를 나눈다.
- setAllowedOriginPatterns는 별표 무늬를 받는다. chrome-extension://* 같은 주소를 허락하려면 Patterns 쪽을 써야 한다.
- setAllowedMethods에 OPTIONS를 넣는다. 브라우저가 진짜 요청 전에 보내는 사전 요청(preflight)이 OPTIONS다.
- addAllowedHeader("*")는 요청 헤더를 모두 허락한다. Authorization을 실어 보내려면 필요하다.
- registerCorsConfiguration("/**", config)는 모든 주소에 이 설정을 쓴다.

### src/main/resources/application.properties

#### 코드
- app.cors.allowed-origins는 우리가 지은 설정 이름이다. 스프링이 원래 아는 키가 아니다.
- 3000과 5173은 리액트 개발 서버 주소다. chrome-extension://*은 브라우저 확장으로 API를 시험할 때 쓴다.
- 주소를 코드가 아니라 설정에 두면 주소가 바뀌어도 코드를 고치지 않는다.

## jwt-auth T1

### src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java

#### 개념 인증 필터
##### 무엇
- 요청마다 Authorization 헤더의 토큰을 검사하는 필터다.
- 토큰이 맞으면 그 회원을 SecurityContext에 넣는다. 그 요청 동안 로그인한 사람으로 취급된다.
##### 왜
- 6장 user-login에서 토큰을 나눠 줬지만 아직 아무도 읽지 않는다.
- 내 정보, 글쓰기처럼 누가 부르는지 알아야 하는 API가 생긴다.
##### 쉬운 방법과 비교
- 컨트롤러마다 헤더를 꺼내 JwtUtil로 검증할 수도 있다. 내 정보 API 하나뿐인 지금은 된다.
- 8장 board-write, 9장 board-update-delete, 10장 reply로 가면 같은 줄이 메서드마다 반복된다.
- 필터에서 한 번 하면 컨트롤러는 @AuthenticationPrincipal로 회원만 받는다.
##### 헷갈리는 개념
- 필터는 누구인지 알아내기만 한다. 들어와도 되는지는 다음 단계의 URL 규칙이 정한다.
- 이름은 Authorization이지만 하는 일은 인증이다. 토큰으로 누구인지 확인하는 일이다.
##### 이 예제에서는
- doFilterInternal에서 헤더를 꺼내고, 검증하고, SecurityContextHolder에 넣는다.

#### 코드
- OncePerRequestFilter는 한 요청에 한 번만 도는 필터의 부모 클래스다.
- header.startsWith(TOKEN_PREFIX)로 Bearer 토큰만 다룬다. 헤더가 없으면 아무것도 하지 않는다.
- substring으로 Bearer 를 떼고 토큰만 JwtUtil.verify에 넘긴다.
- UsernamePasswordAuthenticationToken(user, null, List.of())는 인증이 끝난 사람표다. 매개변수 세 개짜리 생성자라야 인증됨으로 표시된다.
- 두 번째 칸 null은 비밀번호 자리다. 이미 토큰으로 확인했으니 들고 다니지 않는다.
- 세 번째 칸은 권한 목록이다. 지금은 비어 있고 13장 admin-api에서 역할을 넣는다.
- catch가 비어 있다. 토큰이 만료되거나 위조되면 회원을 넣지 않고 그냥 지나간다.
- 여기서 오류를 던지지 않는 이유가 있다. 들어와도 되는지는 URL 규칙이 정하게 맡긴다.
- 만료된 토큰을 들고 있어도 글 목록처럼 열린 주소는 그대로 보인다. 막힌 주소는 다음 단계의 401이 알려 준다.
- 11장 board-owner의 글 상세는 이 덕분에 로그인 없이도, 토큰이 있으면 주인 표시와 함께 열린다.
- filterChain.doFilter는 다음 필터로 넘긴다. 이 줄이 없으면 요청이 컨트롤러까지 가지 않는다.

### src/main/java/com/newtenco/blog/core/util/JwtUtil.java

#### 코드
- JWT.require(ALGORITHM).build().verify(jwt)는 서명과 만료를 함께 검사한다. 틀리면 JWTVerificationException을 던진다.
- getClaim("id").asInt()와 getSubject()로 담아 둔 값을 꺼낸다.
- User.builder()로 id와 username만 채운 User를 만든다. DB를 보지 않는다.
- 이 User는 DB에서 꺼낸 것이 아니라 email이나 password가 비어 있다. 다른 값이 필요하면 id로 다시 조회한다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 Stateless
##### 무엇
- 서버가 로그인 상태를 기억하지 않는 것이다.
- 요청마다 토큰만 보고 그 요청 동안만 회원을 안다. 요청이 끝나면 잊는다.
##### 왜
- Security는 기본으로 세션을 만들어 로그인한 사람을 기억하려 한다.
- 토큰을 쓰는데 세션까지 만들면 기억하는 곳이 둘이 된다. 쓰지도 않을 세션이 쌓인다.
##### 헷갈리는 개념
- STATELESS는 Security가 세션을 만들지도, 세션에서 회원을 찾지도 않는다는 뜻이다.
- 서버 전체가 상태가 없다는 뜻은 아니다. DB는 그대로 있다. 15장 refresh-token은 리프레시 토큰을 DB에 저장한다.
##### 이 예제에서는
- sessionManagement의 SessionCreationPolicy.STATELESS 한 줄이다.

#### 코드
- addFilterBefore는 우리 필터를 UsernamePasswordAuthenticationFilter 앞에 끼운다. Security가 회원을 찾는 단계보다 먼저 SecurityContext를 채운다.
- 필터에 @Component를 붙이지 않고 new로 만든다. 빈으로 만들면 스프링이 Security 밖 일반 필터로 한 번 더 등록한다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 개념 내 정보 조회
##### 무엇
- 토큰의 주인이 자기 정보를 받는 GET /api/users/me다.
- 주소에 id를 적지 않는다. 누구인지는 토큰이 알려 준다.
##### 쉬운 방법과 비교
- GET /api/users/{id}처럼 id를 받으면 남의 번호를 넣어 남의 정보를 볼 수 있다.
- me는 토큰의 id만 쓰므로 그런 일이 없다.
##### 이 예제에서는
- @AuthenticationPrincipal로 필터가 넣은 User를 받고, 그 id로 서비스를 부른다.

#### 코드
- @AuthenticationPrincipal은 SecurityContext에 들어 있는 회원을 매개변수로 꺼낸다.
- 필터가 넣은 것이 User라서 타입을 User로 받는다.
- 토큰이 없으면 sessionUser가 null이다. 지금은 모든 주소가 열려 있어 null에서 500이 난다. 다음 단계에서 막는다.
- 이름을 sessionUser로 쓴다. 세션은 없지만 로그인한 회원이라는 뜻의 관례 이름이다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 토큰에는 email이 없다. 그래서 id로 DB에서 다시 조회한다.
- 토큰은 맞는데 회원이 지워졌으면 404로 알린다.

## jwt-auth T2

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 URL 접근 규칙
##### 무엇
- 주소마다 누구나 되는지, 로그인해야 하는지를 한 곳에 적는 것이다.
- 위에서부터 차례로 맞춰 보고, 처음 맞는 규칙 하나만 쓴다.
##### 왜
- 앞 단계에서는 토큰 없이 내 정보를 부르면 500이 난다.
- 로그인이 필요한 주소를 컨트롤러에 닿기 전에 막아야 한다.
##### 쉬운 방법과 비교
- 컨트롤러에서 sessionUser == null이면 401을 던질 수도 있다.
- 메서드마다 그 줄을 잊지 않아야 한다. 하나라도 빠지면 그 주소는 열린다.
- 규칙 한 곳에 /api/**를 적으면 앞으로 생기는 /api 주소가 모두 자동으로 막힌다.
##### 헷갈리는 개념
- 순서가 중요하다. GET /api/boards/**를 /api/**보다 위에 둬야 글 목록이 열린다.
- 9장 board-update-delete에서 GET /api/boards/*/edit을 그 위에 더하는 것도 같은 이유다.
- permitAll은 로그인 없이도 된다는 뜻이다. 로그인한 사람을 막는다는 뜻이 아니다.
##### 이 예제에서는
- authorizeHttpRequests 안의 requestMatchers 줄들이다.

#### 개념 401 JSON
##### 무엇
- 막힌 요청에 다른 응답과 같은 Resp 모양의 JSON으로 401을 돌려주는 것이다.
##### 왜
- 설정이 없으면 Security는 빈 본문으로 응답한다. 리액트가 보여 줄 문구가 없다.
- GlobalExceptionHandler로 잡으면 될 것 같지만 닿지 않는다.
- @RestControllerAdvice는 컨트롤러에서 난 예외만 잡는다. Security는 그보다 앞의 필터 단계에서 막는다.
- 그래서 필터 단계에서 응답에 JSON을 직접 쓴다. RespFilter가 그 일을 한다.
##### 헷갈리는 개념
- authenticationEntryPoint는 누구인지 모를 때, 즉 로그인이 필요할 때 불린다. 401이다.
- 로그인은 했는데 권한이 없을 때는 다음 단계의 accessDeniedHandler가 불린다. 403이다.
##### 이 예제에서는
- exceptionHandling의 authenticationEntryPoint가 RespFilter.fail(res, 401, ...)을 부른다.

#### 코드
- HttpMethod.OPTIONS를 맨 위에서 연다. 브라우저의 CORS 사전 요청에는 토큰이 실리지 않아서 막으면 본 요청을 못 보낸다.
- /join, /login, /check-username은 로그인 전에 부르는 주소라 연다.
- /h2-console/**는 H2 콘솔 화면이다.
- HttpMethod.GET을 적으면 GET만 연다. 같은 /api/boards라도 POST 글쓰기는 아래 규칙으로 간다.
- authenticated()는 인증된 회원만 통과시킨다. 필터가 SecurityContext를 채운 요청이다.
- anyRequest().permitAll()은 아직 그대로다. 다음 단계에서 바꾼다.
- (req, res, e) -> ...는 람다다. 요청, 응답, 예외를 받아 응답을 직접 쓴다.

### src/main/java/com/newtenco/blog/core/util/RespFilter.java

#### 코드
- 컨트롤러가 아니라서 ResponseEntity를 돌려줄 수 없다. HttpServletResponse에 직접 쓴다.
- setStatus로 상태 코드를, setContentType으로 JSON과 utf-8을 정한다. utf-8이 없으면 한국어가 깨진다.
- JsonMapper는 Jackson 3의 객체를 JSON 글자로 바꾸는 도구다. 패키지가 tools.jackson이다.
- new Resp<>(status, msg, null)로 Resp.fail과 같은 모양을 만든다.
- static 필드 하나로 만들어 두고 같이 쓴다. 요청마다 새로 만들지 않는다.

## jwt-auth T3

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 코드
- anyRequest().denyAll()은 위 규칙에 없는 주소를 모두 막는다.
- 열어 둔 것만 열리고 나머지는 닫힌다. 새 주소를 만들고 규칙을 잊으면 열리는 대신 막힌다.
- 막혀 있으니 잊은 것을 바로 알아챈다. 15장 refresh-token에서 /reissue와 /logout을 규칙에 따로 더하는 이유다.
- 로그인하지 않은 사람이 막히면 401, 로그인한 사람이 막히면 403이 난다.
- accessDeniedHandler는 그 403을 RespFilter로 JSON으로 만든다.
- 13장 admin-api에서 일반 회원이 관리자 주소를 부를 때도 이 403이 나간다.

## board-write T1

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 개념 로그인 사용자 꺼내기
##### 무엇
- 글을 쓴 사람을 요청 body가 아니라 토큰에서 꺼내는 것이다.
##### 왜
- 글에는 작성자가 꼭 있어야 한다. 누가 썼는지를 어디서 알지 정해야 한다.
##### 쉬운 방법과 비교
- body에 userId를 함께 보내게 할 수도 있다. 저장은 된다.
- 하지만 아무 번호나 넣으면 남의 이름으로 글이 써진다.
- 토큰은 서버가 서명했으니 바꿀 수 없다. 그래서 작성자는 토큰에서만 꺼낸다.
- 9장 board-update-delete의 작성자 검사도 같은 sessionUser의 id로 한다.
##### 헷갈리는 개념
- sessionUser는 DB에서 꺼낸 회원이 아니다. 토큰의 id와 username만 채운 User다.
- 그래도 저장에는 충분하다. 글의 user_id 칸에는 id만 들어가기 때문이다.
##### 이 예제에서는
- save 메서드의 @AuthenticationPrincipal User sessionUser를 서비스로 넘긴다.
- POST /api/boards는 GET 규칙에 걸리지 않고 /api/** 규칙으로 간다. 토큰이 없으면 401이다.

#### 코드
- 주소는 목록과 같은 /api/boards다. GET은 목록, POST는 쓰기로 메서드가 가른다.

### src/main/java/com/newtenco/blog/board/Board.java

#### 코드
- User처럼 생성자에 @Builder를 붙인다. toEntity에서 쓴다.

### src/main/java/com/newtenco/blog/board/BoardRequest.java

#### 코드
- toEntity(User user)는 작성자를 바깥에서 받는다. DTO에는 작성자 칸이 없다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 개념 저장 트랜잭션
##### 무엇
- DB를 바꾸는 서비스 메서드는 쓰기 트랜잭션 안에서 돈다.
- 클래스의 readOnly = true를 메서드의 @Transactional이 덮어쓴다. 회원가입에서 본 그 모양이다.
##### 왜
- readOnly 트랜잭션은 바뀐 것을 DB에 내보내지 않는다는 약속이다.
- 그 안에서 저장하거나 고치면 DB에 안 갈 수 있다.
##### 쉬운 방법과 비교
- 글쓰기는 save 한 번이라 @Transactional을 빼도 저장되는 것처럼 보일 수 있다. save가 스스로 트랜잭션을 열기 때문이다.
- 9장 board-update-delete의 글 수정은 save 없이 엔티티 값만 바꾼다. 그때는 쓰기 트랜잭션이 없으면 아무것도 바뀌지 않는다.
- 10장 reply의 글 지울 때 댓글도 지우기처럼 여러 번 바꾸는 일은 중간에 실패하면 모두 되돌려야 한다.
- 그래서 바꾸는 메서드에는 처음부터 @Transactional을 붙이는 습관을 들인다.
##### 이 예제에서는
- 글쓰기 메서드에 @Transactional을 붙인다. 글목록과 글상세는 그대로 readOnly다.

#### 코드
- 저장한 글을 ListDTO로 돌려준다. 목록 한 줄과 같은 모양이라 새 DTO를 만들지 않는다.
- ListDTO는 작성자의 username을 쓴다. sessionUser에 username이 들어 있어서 DB를 다시 보지 않아도 된다.

## board-write T2

### src/main/java/com/newtenco/blog/board/BoardRequest.java

#### 코드
- 제목과 내용에 @NotBlank를 붙여 빈 글을 막는다.
- @Size(max = 100)과 @Size(max = 1000)으로 길이를 막는다. 너무 긴 값이 DB 칸에서 500이 되기 전에 400으로 알린다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @Valid를 @RequestBody 앞에 붙인다. 토큰 검사는 그보다 먼저 필터에서 끝난다. 그래서 토큰 없이 빈 글을 보내면 400이 아니라 401이 난다.

## board-update-delete T1

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- /api/boards/{id}/edit는 수정 화면에 채울 제목과 내용을 주는 주소다.
- 상세 주소와 따로 둔 이유는 다음 단계에서 작성자만 보게 막을 주소이기 때문이다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- 글수정정보는 회원 정보가 필요 없어서 join fetch 없는 findById를 쓴다.
- 돌려주는 모양은 제목과 내용이 이미 들어 있는 ListDTO를 다시 쓴다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 코드
- requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()는 수정 정보 주소만 로그인을 요구한다.
- 이 줄은 GET /api/boards/** permitAll 줄보다 위에 있어야 한다.
- 규칙은 위에서부터 처음 맞는 줄 하나만 쓰기 때문이다.
- 아래에 두면 /api/boards/1/edit가 permitAll에 먼저 걸려 로그인 없이 열린다.
- *는 경로 한 칸만, **는 여러 칸을 뜻한다.

## board-update-delete T2

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 개념 작성자 검사 403
##### 무엇
- 로그인한 사람이 이 글의 주인인지 확인하고, 아니면 403 Forbidden으로 거절하는 것이다.
- 글의 user id와 토큰에서 꺼낸 내 id를 비교한다.
##### 왜
- 앞 단계는 로그인만 하면 남의 글 수정 정보도 볼 수 있었다.
- 주소 규칙은 로그인했는지만 안다. 어느 글의 주인인지는 글을 읽어 봐야 안다.
##### 쉬운 방법과 비교
- 컨트롤러에서 검사해도 지금은 된다.
- 하지만 컨트롤러는 글을 들고 있지 않아서 글을 한 번 더 읽어야 한다.
- 서비스는 어차피 글을 읽으니 같은 자리에서 검사하는 것이 가장 짧다.
- 13장 admin-api에서 관리자는 주인 검사 없이 글을 지운다. 검사가 서비스 메서드마다 있어서 관리자용 서비스에서는 그냥 빼면 된다.
##### 헷갈리는 개념
- 401은 누군지 모를 때다. 토큰이 없거나 틀렸다.
- 403은 누군지는 알지만 이 일을 할 권한이 없을 때다.
- 404 검사가 먼저다. 글이 없으면 주인을 비교할 대상이 없다.
- 그래서 순서는 글 찾기, 없으면 404, 주인 아니면 403이다.
##### 이 예제에서는
- 글수정정보의 if (!board.getUser().getId().equals(sessionUserId)) 줄이다.
- 같은 검사가 T3 글수정, T4 글삭제, 10장 댓글삭제에 그대로 나온다.

#### 코드
- 매개변수로 User 전체가 아니라 sessionUserId만 받는다. 비교에 id만 쓰기 때문이다.
- equals로 비교한다. Integer는 객체라서 ==는 값이 아니라 같은 객체인지를 본다.
- board.getUser().getId()는 회원을 따로 읽지 않는다. LAZY 프록시도 id는 이미 알고 있다.

### src/main/java/com/newtenco/blog/core/handler/errors/ForbiddenException.java

#### 코드
- 새 파일이다. 403을 뜻하는 예외로 NotFoundException과 같은 모양이다.

### src/main/java/com/newtenco/blog/core/handler/GlobalExceptionHandler.java

#### 코드
- ex403은 ForbiddenException을 받아 HttpStatus.FORBIDDEN 403으로 응답한다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- 수정 정보 주소는 authenticated라서 sessionUser가 null일 일이 없다. 그래서 바로 getId()를 부른다.

## board-update-delete T3

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 개념 변경 감지
##### 무엇
- 변경 감지(dirty checking)는 트랜잭션 안에서 읽은 엔티티의 필드를 바꾸면 끝날 때 JPA가 UPDATE를 대신 보내는 것이다.
- JPA는 읽을 때 처음 모습을 기억해 두고, 커밋 직전에 지금 모습과 비교한다.
##### 왜
- 글을 고치려면 DB의 행을 바꿔야 한다.
- 우리가 UPDATE 쿼리를 쓰지 않아도 객체만 바꾸면 반영된다.
##### 쉬운 방법과 비교
- 필드를 바꾼 뒤 boardRepository.save(board)를 불러도 지금은 된다.
- 하지만 이미 DB에 있는 엔티티라 save가 하는 일은 없다. 반영은 커밋 때 변경 감지가 한다.
- 정작 중요한 것은 save가 아니라 트랜잭션이다. @Transactional을 빼면 save를 불러도 반영을 믿을 수 없다.
- 14장 admin-roles에서는 역할 목록 안의 값만 바꾼다. 거기서도 save 없이 변경 감지로 반영된다.
##### 헷갈리는 개념
- BoardService 클래스에는 @Transactional(readOnly = true)가 붙어 있다.
- readOnly 트랜잭션에서는 변경 감지가 일어나지 않는다.
- 그래서 글수정 메서드에 @Transactional을 따로 붙인다. 메서드에 붙인 것이 클래스 것보다 먼저다.
- 이 줄을 빼면 오류 없이 200이 오지만 DB는 그대로다. 가장 찾기 어려운 실수다.
##### 이 예제에서는
- 글수정의 board.update(reqDTO.title(), reqDTO.content()) 줄이다.
- 메서드가 끝나며 커밋될 때 콘솔에 update board_tb 쿼리가 찍힌다.

#### 코드
- 응답은 바뀐 board로 만든다. 커밋 전이지만 객체는 이미 새 값을 들고 있다.

### src/main/java/com/newtenco/blog/board/Board.java

#### 코드
- update는 제목과 내용만 바꾸는 메서드다.
- setter를 열지 않고 바꿀 수 있는 필드를 메서드 하나로 정해 둔다. 작성자나 작성일은 바꿀 수 없다.

### src/main/java/com/newtenco/blog/board/BoardRequest.java

#### 코드
- UpdateDTO는 글쓰기 DTO와 같은 검사를 단다. 수정해도 빈 제목이나 긴 내용은 막아야 한다.
- toEntity가 없다. 새 엔티티를 만들지 않고 있던 엔티티를 바꾸기 때문이다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @PutMapping은 있는 자원을 고치는 요청을 받는다.
- 글 상세와 같은 주소 /api/boards/{id}를 쓰고 방식만 PUT으로 다르다.
- PUT은 GET 규칙에 걸리지 않으니 /api/** authenticated 규칙으로 로그인이 필요하다.

## board-update-delete T4

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- 글삭제도 404, 403 순서로 검사한 뒤 boardRepository.delete(board)를 부른다.
- 지우는 것도 DB를 바꾸는 일이라 @Transactional을 붙인다.
- 지금은 댓글이 없어서 그냥 지워진다. 10장 reply T4에서 댓글 달린 글을 지울 때 문제가 생긴다.

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 코드
- @DeleteMapping은 자원을 지우는 요청을 받는다.
- 돌려줄 데이터가 없어서 Resp.ok(null)로 성공만 알린다.

## reply T1

### src/main/java/com/newtenco/blog/reply/Reply.java

#### 개념 Reply 엔티티
##### 무엇
- 댓글 한 줄을 뜻하는 엔티티다. 누가 썼는지와 어느 글에 달렸는지를 둘 다 가진다.
##### 이 예제에서는
- user와 board 두 필드 모두 @ManyToOne(fetch = FetchType.LAZY)다.
- reply_tb 테이블에 user_id와 board_id 외래키 칸이 생긴다.

#### 코드
- comment는 length = 100으로 댓글 길이를 DB에서도 막는다.

### src/main/java/com/newtenco/blog/board/Board.java

#### 개념 양방향 연관관계
##### 무엇
- 댓글에서 글로 가는 길(@ManyToOne)에 더해 글에서 댓글 목록으로 가는 길(@OneToMany)도 여는 것이다.
##### 왜
- 글 상세에 댓글 목록을 함께 주려면 글에서 댓글로 갈 수 있어야 한다.
##### 쉬운 방법과 비교
- ReplyRepository에 findByBoardId를 만들어 댓글을 따로 읽어도 된다. 쿼리가 두 번이 될 뿐 지금도 잘 된다.
- 차이는 T4에서 난다. 글이 댓글 목록을 알아야 글을 지울 때 댓글도 함께 지우라고 적을 수 있다.
##### 헷갈리는 개념
- mappedBy = "board"는 외래키 주인이 Reply의 board 필드라는 뜻이다.
- 외래키 칸은 reply_tb에만 있다. board_tb에 replies 칸이 생기지 않는다.
- 그래서 board.getReplies().add(reply)만 해서는 DB에 아무것도 저장되지 않는다. 저장은 Reply 쪽에서 한다.
##### 이 예제에서는
- Board의 private List<Reply> replies = new ArrayList<>(); 줄이다.

#### 코드
- 목록을 new ArrayList<>()로 시작해 둔다. 댓글 없는 글에서도 null이 아니라 빈 목록이 된다.

### src/main/java/com/newtenco/blog/board/BoardRepository.java

#### 개념 상세에 댓글 함께 주기
##### 무엇
- 글 하나를 읽을 때 작성자, 댓글, 댓글 작성자까지 쿼리 한 번으로 가져오는 것이다.
##### 왜
- 댓글은 LAZY라서 그냥 두면 댓글 목록과 댓글마다 작성자를 꺼낼 때 쿼리가 따로 나간다.
- open-in-view가 꺼져 있어서 서비스 밖에서 꺼내면 아예 오류가 난다.
##### 쉬운 방법과 비교
- join fetch b.replies로 쓰고 싶어진다.
- 그러면 댓글이 하나도 없는 글은 결과가 0줄이 되어 404가 난다. 샘플 데이터의 1, 2, 3번 글이 그렇다.
- left join fetch는 댓글이 없어도 글 한 줄은 남긴다.
##### 헷갈리는 개념
- join은 양쪽에 짝이 있는 줄만 남긴다. left join은 왼쪽 글을 무조건 남기고 짝이 없으면 빈칸으로 둔다.
- 작성자는 글마다 꼭 있으니 join fetch b.user 그대로 둔다.
- r.user도 left로 쓴다. 앞의 r이 비어 있을 수 있기 때문이다.
##### 이 예제에서는
- findByIdJoinUserAndReplies의 left join fetch b.replies r, left join fetch r.user 두 줄이다.

#### 코드
- 세 따옴표 """ 는 여러 줄 문자열이다. 긴 JPQL을 줄 나눠 읽기 쉽게 쓴다.
- b.replies r처럼 별칭을 붙여야 r.user와 r.id를 이어서 쓸 수 있다.
- order by r.id는 댓글을 쓴 순서로 줄 세운다. 없으면 DB가 주는 순서라 매번 같다는 보장이 없다.
- 메서드 이름을 findByIdJoinUserAndReplies로 바꿨다. 무엇을 함께 읽는지 이름에 드러낸다.

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 코드
- DetailDTO에 List<ReplyDTO> replies 칸이 늘었다.
- ReplyDTO를 DetailDTO 안에 둔 것은 상세 화면에서만 쓰는 댓글 모양이기 때문이다.
- board.getReplies().stream().map(ReplyDTO::new).toList()는 엔티티 목록을 DTO 목록으로 바꾼다.
- ReplyDTO::new는 reply -> new ReplyDTO(reply)를 줄인 것이다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- 글상세가 새 쿼리 findByIdJoinUserAndReplies를 부르도록 바꾼다.

### src/main/resources/db/data.sql

#### 코드
- 4번 글과 5번 글에만 댓글을 넣는다. 1, 2, 3번 글로 댓글 없는 경우를 확인할 수 있다.

## reply T2

### src/main/java/com/newtenco/blog/reply/ReplyService.java

#### 개념 댓글 쓰기
##### 무엇
- 요청의 boardId로 글을 찾고, 로그인한 회원과 묶어 Reply를 저장하는 것이다.
##### 쉬운 방법과 비교
- 글을 찾지 않고 id만 넣은 Board를 만들어 저장해도 쿼리는 줄어든다.
- 하지만 없는 글 번호가 오면 외래키 오류로 500이 난다. 먼저 찾으면 404로 알려줄 수 있다.
##### 이 예제에서는
- 댓글쓰기의 boardRepository.findById(reqDTO.boardId()) 줄이다.

#### 코드
- ReplyService는 BoardRepository도 받는다. 댓글을 달 글을 찾아야 하기 때문이다.
- sessionUser는 토큰에서 만든 객체지만 id가 있어서 그대로 외래키로 쓸 수 있다.

### src/main/java/com/newtenco/blog/reply/ReplyRequest.java

#### 코드
- boardId에는 @NotNull을 단다. 숫자 칸이라 @NotBlank는 쓸 수 없다.
- toEntity(User user, Board board)는 요청에 없는 회원과 글을 밖에서 받아 엔티티를 만든다.

### src/main/java/com/newtenco/blog/reply/ReplyResponse.java

#### 코드
- DTO에 boardId를 넣어 어느 글의 댓글인지 화면이 알 수 있게 한다.

### src/main/java/com/newtenco/blog/reply/ReplyController.java

#### 코드
- 주소는 /api/replies다. 글 번호는 주소가 아니라 본문 boardId로 받는다.
- /api/** 규칙에 걸려 로그인이 필요하다.

## reply T3

### src/main/java/com/newtenco/blog/reply/ReplyService.java

#### 개념 댓글 삭제
##### 무엇
- 글 삭제와 같은 순서다. 댓글을 찾아 없으면 404, 내 댓글이 아니면 403, 맞으면 지운다.
##### 이 예제에서는
- 댓글삭제의 reply.getUser().getId().equals(sessionUserId) 줄이다.

### src/main/java/com/newtenco/blog/reply/ReplyController.java

#### 코드
- DELETE /api/replies/{id}로 댓글 번호를 주소에서 받는다.

## reply T4

### src/main/java/com/newtenco/blog/board/Board.java

#### 개념 cascade REMOVE
##### 무엇
- 부모 엔티티를 지울 때 목록에 든 자식 엔티티도 함께 지우라는 설정이다.
##### 왜
- 9장의 글삭제로 댓글 달린 4번 글을 지우면 오류가 난다.
- reply_tb의 board_id가 4번 글을 가리키고 있어 DB가 외래키 제약으로 막는다. 응답은 500이다.
##### 쉬운 방법과 비교
- 글삭제에서 replyRepository로 댓글을 먼저 지우고 글을 지워도 된다.
- 그런데 글을 지우는 곳이 늘면 그곳마다 이 줄을 잊지 말아야 한다.
- 13장 admin-api의 관리자 글 삭제가 그렇다. cascade가 있어서 거기서는 boardRepository.delete만 부르면 된다.
- 댓글이 아주 많으면 cascade는 댓글을 다 읽고 하나씩 DELETE를 보낸다. 그때는 DB의 ON DELETE CASCADE가 낫다.
##### 헷갈리는 개념
- orphanRemoval은 다르다. 부모는 그대로 두고 목록에서 뺀 자식을 지운다.
- cascade는 부모에서 자식으로만 간다. 댓글을 지운다고 글이 지워지지 않는다.
##### 이 예제에서는
- @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE) 줄이다.
- BoardService 글삭제 코드는 하나도 바뀌지 않는다.

## board-owner T1

### src/main/java/com/newtenco/blog/board/BoardController.java

#### 개념 선택 인증
##### 무엇
- 로그인 없이도 열리는 주소에서, 토큰이 있으면 누군지 알아내고 없으면 손님으로 두는 것이다.
##### 왜
- 글 상세는 손님도 봐야 해서 permitAll이다.
- 그런데 화면은 내 글이면 수정, 삭제 버튼을 보여주고 싶다.
##### 쉬운 방법과 비교
- 로그인용 상세 주소를 하나 더 만들 수도 있다. 같은 일을 하는 주소가 둘이 되어 고칠 곳도 둘이 된다.
- 화면이 응답의 userId와 내 id를 비교해도 된다. 그러면 화면이 내 id를 따로 들고 있어야 한다.
##### 헷갈리는 개념
- permitAll이어도 JWT 필터는 돈다. 토큰이 맞으면 인증 정보가 채워진다.
- 토큰이 없거나 틀리면 필터는 그냥 지나가고 @AuthenticationPrincipal은 null을 준다.
- owner는 버튼을 보여줄지만 정한다. 실제로 막는 것은 9장의 403 검사다.
##### 이 예제에서는
- sessionUser == null ? null : sessionUser.getId() 줄이다.

#### 코드
- 조건 ? 참일 때 : 거짓일 때는 삼항 연산자다. 손님이면 null, 로그인했으면 내 id다.
- null 검사 없이 sessionUser.getId()를 부르면 손님 요청에서 NullPointerException으로 500이 난다.

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 개념 owner 계산
##### 무엇
- 이 글이 요청한 사람의 글인지 서버가 계산해 boolean 칸으로 주는 것이다.
##### 헷갈리는 개념
- board.getUser().getId().equals(sessionUserId)는 손님일 때 equals(null)이 되고 false가 나온다.
- 반대로 sessionUserId.equals(...)로 쓰면 손님일 때 null에 메서드를 불러 오류가 난다.
- 그래서 절대 null이 아닌 쪽을 앞에 둔다.
##### 이 예제에서는
- DetailDTO 생성자의 owner 칸이다.

#### 코드
- owner는 Boolean이 아니라 boolean이다. 참 거짓 둘 중 하나만 있고 null은 없다.
- DetailDTO 생성자가 sessionUserId를 더 받는다. 같은 글이라도 보는 사람에 따라 owner가 달라지기 때문이다.

### src/main/java/com/newtenco/blog/board/BoardService.java

#### 코드
- 글상세가 sessionUserId를 받아 DTO로 넘기기만 한다. 손님이면 null이 그대로 간다.

## board-owner T2

### src/main/java/com/newtenco/blog/board/BoardResponse.java

#### 코드
- ReplyDTO에도 owner 칸을 더해 내 댓글에만 삭제 버튼을 보여줄 수 있게 한다.
- ReplyDTO::new를 reply -> new ReplyDTO(reply, sessionUserId)로 바꿨다.
- 생성자가 값을 두 개 받게 되어 메서드 참조로는 sessionUserId를 넘길 수 없기 때문이다.

## user-update T1

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 회원수정도 9장처럼 @Transactional과 변경 감지로 반영한다. save는 부르지 않는다.
- 회원을 토큰의 id로 찾는다. 주소에 회원 번호가 없으니 남의 정보를 고칠 길이 없다.
- 비밀번호 자리에 user.getPassword()를 넣어 지금은 이메일만 바뀌게 한다.

### src/main/java/com/newtenco/blog/user/User.java

#### 코드
- update(email, password)는 바꿀 수 있는 두 칸만 받는다. 유저네임은 바꿀 수 없다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- PUT /api/users/me는 내 정보 주소와 같은 주소에 방식만 PUT이다.
- me는 토큰의 주인이라는 뜻이다. 번호를 주소에 쓰지 않는다.

## user-update T2

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 개념 빈 비밀번호 유지
##### 무엇
- 비밀번호 칸을 비워 보내면 원래 비밀번호를 그대로 두는 규칙이다.
##### 왜
- 이메일만 고치려는 사람에게 비밀번호를 다시 치게 하면 불편하다.
- 비운 칸을 그대로 저장하면 빈 비밀번호로 바뀌어 로그인할 수 없게 된다.
##### 쉬운 방법과 비교
- 비밀번호를 늘 필수로 받으면 코드는 쉽다. 대신 이메일만 고칠 때도 비밀번호를 쳐야 한다.
- 이메일 수정과 비밀번호 수정 주소를 나누어도 된다. 화면에 저장 버튼이 하나라 이 예제는 한 주소로 받는다.
##### 이 예제에서는
- isPasswordChange()가 null도 아니고 빈 글자도 아닐 때만 true를 준다.

#### 코드
- record 안에도 메서드를 만들 수 있다. 판단을 DTO가 하면 서비스 코드가 한 줄로 읽힌다.
- password != null을 먼저 본다. null에 isEmpty()를 부르면 오류가 나기 때문이다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 바꿀 때는 passwordEncoder.encode로 암호화한 값을 넣는다.
- 그냥 넣으면 로그인의 matches가 평문과 해시를 비교하게 되어 실패한다.
- 안 바꿀 때는 user.getPassword()로 이미 암호화된 원래 값을 다시 넣는다.

## user-update T3

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 개념 조건부 검증
##### 무엇
- 값이 있을 때만 규칙을 검사하고, 비어 있으면 통과시키는 검사다.
##### 왜
- 비밀번호 칸은 비워도 되지만 쓴다면 4자에서 20자여야 한다.
##### 쉬운 방법과 비교
- @Size(min = 4, max = 20)을 먼저 떠올린다.
- @Size는 null은 통과시키지만 빈 글자는 길이 0이라 막는다.
- 화면의 빈 입력칸은 null이 아니라 빈 글자로 오니 빈 비밀번호 유지가 깨진다.
- 검사 그룹이나 직접 만든 검사기로도 할 수 있지만 정규식 한 줄이 가장 짧다.
##### 헷갈리는 개념
- 정규식 ^$|^.{4,20}$는 빈 글자이거나 4자에서 20자인 글자라는 뜻이다.
- ^는 시작, $는 끝, |는 또는, .{4,20}은 아무 글자 4개에서 20개다.
- @Pattern도 null은 검사하지 않고 통과시킨다. 그래서 null과 빈 글자 둘 다 유지로 간다.
- 이 둘은 T2의 isPasswordChange가 false로 보는 두 경우와 똑같다.
##### 이 예제에서는
- UpdateDTO password 칸의 @Pattern 줄이다.

#### 코드
- 이메일은 늘 필요하니 회원가입과 같은 @NotBlank와 @Email을 단다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- @Valid를 더해야 DTO에 단 검사가 실제로 돈다.

## admin-api T1

### src/main/java/com/newtenco/blog/user/User.java

#### 개념 역할
##### 무엇
- 역할(role)은 회원이 할 수 있는 일의 묶음에 붙인 이름이다. 이 예제에는 USER와 ADMIN이 있다.
- 한 회원이 역할을 여러 개 가질 수 있다. cos는 USER와 ADMIN을 둘 다 가진다.
##### 쉬운 방법과 비교
- user_tb에 role 칸 하나를 두고 "USER,ADMIN"처럼 쉼표로 이어 써도 지금은 된다.
- 읽을 때마다 쪼개고 쓸 때마다 이어 붙여야 한다.
- 14장 admin-roles에서 역할을 바꾸고 검사할 때 이 일이 늘어난다.
- @ElementCollection은 역할 한 개를 한 줄로 저장하고 자바에서는 List로 바로 쓰게 해 준다.
##### 헷갈리는 개념
- @ElementCollection은 엔티티가 아닌 값 목록이다. Role 엔티티나 RoleRepository가 없다.
- 역할은 회원에 딸린 값이라 회원과 함께 저장되고 함께 지워진다.
##### 이 예제에서는
- roles 필드와 user_role_tb 테이블이다.

#### 코드
- @CollectionTable(name = "user_role_tb", joinColumns = @JoinColumn(name = "user_id"))는 역할을 담을 테이블과 회원을 가리킬 칸 이름을 정한다.
- @Column(name = "role")은 역할 글자를 담을 칸 이름이다.
- fetch = FetchType.EAGER는 회원을 읽을 때 역할도 바로 읽는다. 역할은 회원을 쓸 때 거의 늘 필요하다.
- 처음 값을 new ArrayList<>(List.of("USER"))로 둔다. 회원가입하면 따로 넣지 않아도 USER가 된다.
- List.of만 쓰지 않고 ArrayList로 감싼다. List.of는 바꿀 수 없는 목록이라 14장에서 값을 바꿀 수 없다.
- 빌더 생성자는 roles가 null이 아닐 때만 덮어쓴다. 빌더에서 roles를 안 주면 처음 값 USER가 남는다.

### src/main/java/com/newtenco/blog/core/util/JwtUtil.java

#### 코드
- withClaim("roles", user.getRoles())로 토큰에 역할 목록을 넣는다.
- 필터는 DB를 읽지 않고 토큰만 보고 권한을 정하기 때문이다.
- asList(String.class)는 토큰의 roles 칸을 다시 문자열 목록으로 꺼낸다.
- 토큰의 역할은 토큰을 만든 순간의 값이다. 14장에서 역할을 바꿔도 다시 로그인하기 전까지 옛 토큰에는 옛 역할이 남는다.

### src/main/resources/db/data.sql

#### 코드
- ssar는 USER, cos는 USER와 ADMIN을 갖도록 user_role_tb에 넣는다.
- 회원 줄을 먼저 넣고 역할 줄을 넣는다. user_id가 있는 회원을 가리켜야 하기 때문이다.

## admin-api T2

### src/main/java/com/newtenco/blog/user/User.java

#### 개념 hasRole
##### 무엇
- 주소 규칙에서 이 역할을 가진 사람만 들어오게 하는 검사다.
##### 헷갈리는 개념
- hasRole("ADMIN")은 실제로 ROLE_ADMIN이라는 권한이 있는지 본다. 앞에 ROLE_을 스스로 붙인다.
- 그래서 권한을 만들 때 "ROLE_" + role로 붙여 줘야 한다.
- 빼먹으면 권한 이름이 ADMIN이 되어 관리자도 403을 받는다.
- hasAuthority("ROLE_ADMIN")은 붙이지 않고 그대로 비교한다. 결과는 같다.
- DB와 토큰에는 ROLE_ 없이 USER, ADMIN만 둔다. 붙이는 일은 스프링에 넘길 때 한 번만 한다.
##### 이 예제에서는
- getAuthorities()의 new SimpleGrantedAuthority("ROLE_" + role) 줄이다.

#### 코드
- SimpleGrantedAuthority는 스프링 시큐리티가 권한 하나로 알아보는 객체다.

### src/main/java/com/newtenco/blog/core/filter/JwtAuthorizationFilter.java

#### 코드
- 세 번째 인자를 List.of()에서 user.getAuthorities()로 바꿨다.
- 빈 목록이면 로그인은 했지만 권한이 하나도 없는 사람이 된다. 그러면 hasRole 검사를 모두 통과하지 못한다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 관리자 전용 주소
##### 무엇
- /api/admin/으로 시작하는 주소를 ADMIN만 쓰게 묶는 것이다.
##### 쉬운 방법과 비교
- 관리자 메서드마다 서비스에서 역할을 검사해도 된다.
- 주소를 한곳에 모으면 규칙 한 줄로 끝나고, 관리자 메서드를 늘려도 잊을 일이 없다.
##### 헷갈리는 개념
- 이 줄은 /api/** 줄보다 위에 있어야 한다. 아래에 두면 /api/**가 먼저 맞아 USER도 들어온다.
- 로그인 안 한 사람은 401, 로그인했지만 ADMIN이 아닌 사람은 403을 받는다.
##### 이 예제에서는
- .requestMatchers("/api/admin/**").hasRole("ADMIN") 줄이다.

#### 코드
- /api/** 규칙을 authenticated에서 hasAnyRole("USER", "ADMIN")으로 바꿨다. 역할이 하나라도 있어야 들어온다.

### src/main/java/com/newtenco/blog/admin/AdminController.java

#### 코드
- 새 파일이다. 관리자 기능을 admin 패키지에 따로 모은다.
- /api/admin/hello는 관리자 주소 막기가 되는지 확인하는 가장 작은 주소다.

## admin-api T3

### src/main/java/com/newtenco/blog/admin/AdminService.java

#### 코드
- 새 파일이다. 관리자 글 삭제는 주인 검사가 없다. 주소 규칙에서 ADMIN만 들어오기 때문이다.
- BoardService의 글삭제를 다시 쓰지 않는다. 그 메서드에는 403 주인 검사가 들어 있다.
- 댓글 달린 글도 10장의 cascade REMOVE 덕분에 delete 한 줄로 지워진다.

### src/main/java/com/newtenco/blog/admin/AdminController.java

#### 코드
- DELETE /api/admin/boards/{id}로 관리자 삭제 주소를 일반 삭제 주소와 나눈다.
- 이 메서드는 sessionUser가 필요 없어서 받지 않는다.

## admin-roles T1

### src/main/java/com/newtenco/blog/user/User.java

#### 개념 컬렉션 값 바꾸기
##### 무엇
- 엔티티가 가진 목록을 새 목록으로 바꾸지 않고, 같은 목록을 비운 뒤 새 값을 채우는 것이다.
##### 왜
- roles 목록은 JPA가 읽어 와서 지켜보는 목록이다.
- 같은 목록 안의 값이 바뀌면 변경 감지가 user_role_tb를 고쳐 준다.
##### 쉬운 방법과 비교
- this.roles = roles처럼 목록을 통째로 바꿔 끼우는 것이 먼저 떠오른다.
- 지금은 대개 동작하지만 JPA가 지켜보던 목록 객체가 사라지고 들어온 목록이 그대로 엔티티 안에 들어간다.
- T2에서는 List.copyOf로 만든 목록을 넘긴다. 이 목록은 바꿀 수 없어서 바꿔 끼우면 다음 역할 수정의 clear에서 오류가 난다.
- clear와 addAll은 엔티티가 자기 목록을 끝까지 쥐고 있게 한다.
##### 이 예제에서는
- updateRoles의 this.roles.clear(); this.roles.addAll(roles); 두 줄이다.

### src/main/java/com/newtenco/blog/admin/AdminService.java

#### 코드
- 관리자_역할수정도 @Transactional과 변경 감지로 반영한다.
- 바뀐 역할은 그 회원이 다음에 로그인할 때 받는 토큰부터 들어간다. 지금 가진 토큰은 그대로다.
- 15장 refresh-token부터는 재발급 때 DB의 회원으로 새 토큰을 만들어 그때도 새 역할이 들어간다.

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 코드
- RolesDTO는 역할 이름 목록 하나만 받는다. 예 {"roles": ["USER", "ADMIN"]}

### src/main/java/com/newtenco/blog/user/UserResponse.java

#### 코드
- RolesDTO는 바뀐 뒤 역할을 회원 id, 유저네임과 함께 돌려준다.

### src/main/java/com/newtenco/blog/admin/AdminController.java

#### 코드
- PUT /api/admin/users/{id}로 어느 회원의 역할을 바꿀지 주소에서 받는다.

## admin-roles T2

### src/main/java/com/newtenco/blog/admin/AdminService.java

#### 개념 역할 검사
##### 무엇
- 요청한 역할이 허용된 이름인지, 지금과 다른지 확인하고 아니면 400으로 거절하는 것이다.
##### 왜
- 앞 단계는 "MANAGER" 같은 없는 역할도 그대로 저장했다.
- 같은 역할을 다시 보내도 성공으로 답해서 무엇이 바뀌었는지 알 수 없었다.
##### 쉬운 방법과 비교
- 지금 목록과 요청 목록을 List의 equals로 비교하고 싶어진다.
- List는 순서까지 본다. ["USER", "ADMIN"]과 ["ADMIN", "USER"]가 다르다고 나온다.
- Set은 순서와 중복을 보지 않고 무엇이 들었는지만 비교한다.
- 역할 이름을 enum으로 만들 수도 있다. 그러면 틀린 이름은 JSON을 읽을 때 막히지만 오류 문구를 우리가 정하기 어렵다.
##### 이 예제에서는
- new HashSet<>(user.getRoles()).equals(requestedRoles) 줄이 같은 역할 검사다.
- ALLOWED_ROLES.containsAll(requestedRoles) 줄이 허용 이름 검사다.

#### 코드
- Set.of("USER", "ADMIN")은 바꿀 수 없는 집합이다. static final로 두어 한 번만 만든다.
- LinkedHashSet은 중복을 지우면서 들어온 순서는 지킨다. 저장되는 역할 순서가 요청과 같아진다.
- containsAll은 요청한 역할이 모두 허용 목록 안에 있는지 본다.
- List.copyOf(requestedRoles)는 Set을 다시 List로 바꿔 updateRoles에 넘긴다.
- 두 검사 모두 BadRequestException으로 400을 준다. 요청 값이 틀렸다는 뜻이다.

### src/main/java/com/newtenco/blog/user/UserRequest.java

#### 코드
- @NotEmpty는 목록이 null이거나 비었으면 막는다. 역할이 하나도 없는 회원은 어떤 주소도 못 쓰게 된다.
- 목록에는 @NotBlank를 쓸 수 없다. @NotBlank는 글자용이다.

### src/main/java/com/newtenco/blog/admin/AdminController.java

#### 코드
- @Valid를 더해 @NotEmpty 검사를 켠다.

## refresh-token T1

### src/main/java/com/newtenco/blog/auth/RefreshToken.java

#### 개념 토큰 저장
##### 무엇
- 리프레시 토큰은 액세스 토큰을 다시 받을 때만 쓰는 오래 사는 토큰이다.
- 서버 DB에 저장해 두고, 요청이 오면 DB에 있는지로 진짜인지 판단한다.
##### 왜
- 지금 액세스 토큰은 24시간 산다. 그동안 훔쳐 가도 막을 방법이 없다.
- JWT는 서버가 기억하지 않는 토큰이라 로그아웃해도 무를 수 없다.
##### 쉬운 방법과 비교
- 액세스 토큰을 2주짜리로 길게 주면 리프레시 토큰 없이도 로그인이 오래 간다.
- 그러면 훔친 토큰도 2주 동안 쓸 수 있고 로그아웃으로 끊을 수 없다.
- 액세스 토큰은 짧게 두고, 오래 사는 리프레시 토큰은 DB에 두어 언제든 지울 수 있게 한다.
- 혼자 쓰는 작은 서비스라면 긴 액세스 토큰 하나가 더 단순하다.
##### 헷갈리는 개념
- 리프레시 토큰은 JWT가 아니다. UUID로 만든 아무 뜻 없는 긴 글자다.
- 안에 정보가 없으니 DB에서 찾아야만 누구 것인지 안다. 그래서 DB에서 지우면 바로 못 쓴다.
- 액세스 토큰은 매 요청마다, 리프레시 토큰은 재발급과 로그아웃에만 보낸다.
##### 이 예제에서는
- refresh_token_tb 테이블에 token, user_id, expires_at이 저장된다.

#### 코드
- token에 unique = true를 달아 같은 값이 두 번 저장되지 않게 한다. token으로 찾기 때문이다.
- @ManyToOne인 이유는 회원 한 명이 여러 기기에서 로그인해 토큰을 여러 개 가질 수 있기 때문이다.
- expiresAt은 만료 시각이다. 리프레시 토큰은 JWT가 아니라서 만료를 우리가 저장해야 한다.

### src/main/java/com/newtenco/blog/auth/RefreshTokenService.java

#### 코드
- 새 파일이다. 리프레시 토큰을 만들고 지우는 일을 auth 패키지에 모은다.
- UUID.randomUUID().toString()은 겹칠 일이 거의 없는 무작위 글자를 만든다.
- EXPIRATION_TIME은 14일이다. 1000L은 long 숫자다. int로 곱하면 14일이 int 범위를 넘어 틀린 값이 된다.
- HEADER는 응답 헤더 이름 Refresh-Token이다. 컨트롤러 두 곳에서 같이 쓰려고 상수로 둔다.

### src/main/java/com/newtenco/blog/user/UserService.java

#### 코드
- 로그인에 @Transactional을 붙였다. 이제 로그인이 리프레시 토큰을 저장하기 때문이다.
- 클래스의 readOnly 그대로면 저장이 막힌다.
- 로그인할 때마다 새 리프레시 토큰을 하나씩 만든다.

### src/main/java/com/newtenco/blog/user/UserResponse.java

#### 코드
- LoginDTO에 refreshToken 칸을 더한다. 서비스가 만든 두 토큰을 컨트롤러로 함께 넘긴다.

### src/main/java/com/newtenco/blog/user/UserController.java

#### 코드
- 리프레시 토큰도 액세스 토큰처럼 응답 헤더에 담는다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 코드
- setExposedHeaders에 Refresh-Token을 더한다. 더하지 않으면 브라우저 자바스크립트가 이 헤더를 읽지 못한다.

## refresh-token T2

### src/main/java/com/newtenco/blog/auth/RefreshTokenService.java

#### 개념 재발급 회전
##### 무엇
- 리프레시 토큰으로 새 액세스 토큰을 받을 때 리프레시 토큰도 새것으로 바꾸고 옛것은 지우는 것이다.
- 회전(rotation)이라고 부른다. 리프레시 토큰은 한 번만 쓸 수 있다.
##### 왜
- 리프레시 토큰을 계속 다시 쓰게 두면 훔친 토큰으로 14일 내내 새 액세스 토큰을 받을 수 있다.
##### 쉬운 방법과 비교
- 리프레시 토큰은 그대로 두고 액세스 토큰만 새로 줘도 지금은 동작한다.
- 그러면 훔친 리프레시 토큰을 막을 길이 로그아웃뿐이다.
- 회전하면 진짜 주인이 한 번 재발급하는 순간 훔친 옛 토큰은 쓸 수 없게 된다.
##### 헷갈리는 개념
- 만료 검사를 지우기 전에 한다.
- 지우고 나서 만료 예외를 던지면 트랜잭션이 롤백되어 지운 것이 되살아난다.
- 그래서 지웠다고 생각한 줄이 실제로는 지워지지 않는다. 검사를 통과한 토큰만 지운다.
- 액세스 토큰은 DB의 회원으로 새로 만든다. 그래서 14장에서 바뀐 역할이 재발급 때 토큰에 들어간다.
##### 이 예제에서는
- 재발급의 isExpired 검사, delete, 발급(user) 세 줄 순서다.

#### 코드
- 없는 토큰과 만료된 토큰 모두 같은 문구로 401을 준다. 어느 쪽이든 할 일은 다시 로그인이다.
- 같은 클래스의 발급(user)을 불러 새 리프레시 토큰을 저장한다. 하나의 트랜잭션 안에서 지우고 새로 만든다.

### src/main/java/com/newtenco/blog/auth/RefreshToken.java

#### 코드
- isExpired는 만료 시각이 지금보다 앞이면 true다. before는 Timestamp끼리 앞뒤를 비교한다.

### src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java

#### 코드
- findByTokenJoinUser는 토큰과 회원을 함께 읽는다. 새 액세스 토큰에 회원의 유저네임과 역할이 필요하기 때문이다.

### src/main/java/com/newtenco/blog/auth/AuthController.java

#### 코드
- 새 파일이다. 재발급은 로그인처럼 새 토큰 두 개를 응답 헤더에 담는다.
- 본문은 data 없이 성공만 알린다.

### src/main/java/com/newtenco/blog/auth/AuthRequest.java

#### 코드
- 리프레시 토큰은 헤더가 아니라 본문 refreshToken 칸으로 받는다. Authorization 헤더는 액세스 토큰 자리다.

### src/main/java/com/newtenco/blog/auth/AuthResponse.java

#### 코드
- ReissueDTO는 서비스가 만든 두 토큰을 컨트롤러로 넘기는 상자다.

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 코드
- POST /reissue를 permitAll로 연다. 재발급은 액세스 토큰이 만료된 뒤에 부르는 주소라서 로그인을 요구할 수 없다.

## refresh-token T3

### src/main/java/com/newtenco/blog/core/SecurityConfig.java

#### 개념 로그아웃
##### 무엇
- 서버에 저장된 리프레시 토큰을 지워 더는 재발급을 못 하게 하는 것이다.
##### 쉬운 방법과 비교
- 화면에서 토큰만 지우면 이 브라우저에서는 로그아웃된 것처럼 보인다.
- 하지만 토큰을 훔쳐 간 쪽은 계속 재발급을 받을 수 있다. 서버에서 지워야 끊긴다.
##### 헷갈리는 개념
- 스프링 시큐리티는 처음부터 POST /logout을 처리하는 필터를 켜 둔다.
- 그 필터가 먼저 요청을 가로채서 우리가 만든 컨트롤러까지 오지 않는다.
- 그래서 http.logout(logout -> logout.disable())로 기본 로그아웃을 끈다.
- 로그아웃해도 이미 받은 액세스 토큰은 만료까지 살아 있다. 그래서 액세스 토큰을 짧게 두는 것이 중요하다.
##### 이 예제에서는
- http.logout 줄과 /logout을 permitAll에 더한 줄이다.

#### 코드
- requestMatchers(HttpMethod.POST, "/reissue", "/logout")처럼 주소를 여러 개 이어 쓸 수 있다.

### src/main/java/com/newtenco/blog/auth/RefreshTokenService.java

#### 코드
- ifPresent는 찾았을 때만 지운다. 없는 토큰이어도 오류 없이 성공으로 끝난다.
- 로그아웃은 두 번 눌러도 결과가 같아야 해서 404를 주지 않는다.
- refreshTokenRepository::delete는 찾은 토큰을 delete에 넘기는 메서드 참조다.

### src/main/java/com/newtenco/blog/auth/RefreshTokenRepository.java

#### 코드
- findByToken은 이름만으로 쿼리가 만들어진다. 로그아웃은 회원이 필요 없어서 join fetch를 쓰지 않는다.

### src/main/java/com/newtenco/blog/auth/AuthController.java

#### 코드
- 로그아웃도 재발급과 같은 RefreshDTO로 리프레시 토큰을 받는다.
