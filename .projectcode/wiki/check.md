# 점검

어긋난 것 없음

| 점검 | 결과 |
|---|---|
| 장 상태 | 모든 장(15장) 완료. tasks.md 모두 체크, done 태그 있음 |
| 진행 중 | 없음 |
| 장 자료 | 완료인 15장 모두에 captures.md, tree.md, summary-code.md 있음 |
| 사진 | captures.md의 파일 65개 모두 있음 |
| 미룬 것 | 없음 |
| 테스트 | 모든 장의 테스트 81개 모두 통과 |

참고
- board-list T1, T2 사진은 API가 아직 없어 404를 남겼다. 사용자가 그대로 두기로 정했다
- user-join T2 사진은 암호화된 비밀번호를 보일 수 없어 see에서 뺐다. S3 테스트가 DB 값을 확인한다
- user-login T1, T2 사진은 401과 jwt.io 화면을 see에서 뺐다. S3, S4 테스트가 확인한다
- board-write T1 사진은 목록 첫 줄 확인을 see에서 뺐다. S2 테스트가 확인한다
- board-update-delete T1, T3, T4 사진은 두 번째 요청이 필요한 확인(401, 상세 반영, 상세 404)을 see에서 뺐다. S7, S2, S3 테스트가 확인한다
- reply 사진은 두 번째 요청이 필요한 확인을 see에서 뺐다. ReplyTest가 확인한다
- erd는 refresh-token 장 끝 기준이다. 테이블 5개(user_tb, user_role_tb, board_tb, reply_tb, refresh_token_tb)
- 커밋 Touches 꼬리표는 장마다 "파일을 처음 만든 장"과 "고친 줄을 만든 장" 기준이 섞여 있다. 이력은 다시 쓰지 않았다
- jwt-auth 테스트 작성(75132b4)은 BoardDetailTest를, admin-api 테스트 작성(8561306)은 JwtAuthTest와 UserJoinTest를 고쳤는데 Touches가 없다. 이력은 다시 쓰지 않았다
- board-list spec의 응답 예시는 글 3개만 적혀 있다. 그 장 시점 응답은 글 5개다. spec은 장 시점 문서라 고치지 않았다
- board-list S2 테스트는 board-paging부터 첫 쪽 3개를 확인한다. 테스트 이름에 그렇게 적었다
- 본문이 깨진 JSON 요청은 지금 500 관리자에게 문의하세요가 된다(HttpMessageNotReadableException). 고치려면 projectcode-fix로 board-detail에 400 처리를 더한다
