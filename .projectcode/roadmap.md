# 로드맵

강의: 블로그 REST API로 배우는 Spring Boot, JPA, Security, JWT
대상: Java와 SQL 기초를 알고 Spring MVC를 써 본 학생
결과물: 리액트 블로그 따라치기가 그대로 호출하는 블로그 API 서버

## 선수 지식
- Java: 클래스, 객체, 컬렉션, 인터페이스, 어노테이션 읽기
- SQL: SELECT, INSERT, UPDATE, DELETE, JOIN
- Spring MVC: 컨트롤러, @GetMapping, @PostMapping, @RequestBody, @PathVariable

## 학습 목표
- JPA 엔티티, 연관관계, Repository, 페이징
- 공통 응답 모양과 전역 예외 처리
- Bean Validation
- Spring Security 설정, BCrypt
- JWT 발급과 검증 필터, 인증과 권한
- 리프레시 토큰 재발급과 로그아웃

## 장
| 번호 | 장 ID | 이름 | 종류 | 배울 것 | 상태 |
|---|---|---|---|---|---|
| 1 | project-setup | 프로젝트 뼈대 | 기능 | Gradle 구조, Spring Boot 실행, H2 설정, 테스트 실행 | 진행 중 |
| 2 | board-list | 글 목록 | 기능 | JPA 엔티티, ManyToOne, Repository, data.sql, 공통 응답 Resp | 대기 |
| 3 | board-detail | 글 상세 | 기능 | 응답 DTO, 사용자 예외, 전역 예외 처리 404 | 대기 |
| 4 | board-paging | 목록 쪽 나누기와 검색 | 기능 | Pageable, Page, 제목 검색 쿼리 | 대기 |
| 5 | user-join | 회원가입 | 기능 | Bean Validation, Security 기본 설정, BCrypt, 아이디 중복 확인 | 대기 |
| 6 | user-login | 로그인과 JWT 발급 | 기능 | JWT 만들기, 응답 헤더 Authorization, CORS 노출 헤더 | 대기 |
| 7 | jwt-auth | JWT 인증 필터 | 기능 | 인증 필터, Stateless, URL 접근 규칙, 401 JSON, 내 정보 조회 | 대기 |
| 8 | board-write | 글쓰기 | 기능 | 로그인 사용자 꺼내기, 저장 트랜잭션 | 대기 |
| 9 | board-update-delete | 글 수정과 삭제 | 기능 | 작성자 검사 403, 변경 감지 | 대기 |
| 10 | reply | 댓글 | 기능 | Reply 엔티티, 댓글 쓰기와 삭제, 상세에 댓글 함께 주기 | 대기 |
| 11 | board-owner | 내 글 내 댓글 표시 | 기능 | 선택 인증, owner 계산 | 대기 |
| 12 | user-update | 내 정보 수정 | 기능 | 빈 비밀번호 유지, 조건부 검증 | 대기 |
| 13 | admin-api | 관리자 API | 기능 | 역할, hasRole, 관리자 전용 주소 | 대기 |
| 14 | refresh-token | 리프레시 토큰 | 기능 | 토큰 저장, 재발급 회전, 로그아웃 | 대기 |

## 미룬 것
- 없음

## 하지 않는 것
- 배포
- 운영 DB (MySQL 등)
- 리액트 화면
- 서버 확인용 주소
