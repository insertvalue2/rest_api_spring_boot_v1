# 점검

어긋난 것 없음

| 점검 | 결과 |
|---|---|
| 장 상태 | project-setup부터 user-update까지 열두 장 완료. tasks.md 모두 체크, done 태그 있음 |
| 진행 중 | 없음 (admin-api spec 승인 대기) |
| 장 자료 | 완료인 열두 장에 captures.md, tree.md, summary-code.md 있음 |
| 사진 | captures.md의 파일 54개 모두 있음 |
| 미룬 것 | 없음 |
| 테스트 | 완료인 장의 테스트 63개 모두 통과 |

참고
- board-list T1, T2 사진은 API가 아직 없어 404를 남겼다. 사용자가 그대로 두기로 정했다
- user-join T2 사진은 암호화된 비밀번호를 보일 수 없어 see에서 뺐다. S3 테스트가 DB 값을 확인한다
- user-login T1, T2 사진은 401과 jwt.io 화면을 see에서 뺐다. S3, S4 테스트가 확인한다
- board-write T1 사진은 목록 첫 줄 확인을 see에서 뺐다. S2 테스트가 확인한다
- board-update-delete T1, T3, T4 사진은 두 번째 요청이 필요한 확인(401, 상세 반영, 상세 404)을 see에서 뺐다. S7, S2, S3 테스트가 확인한다
- reply 사진은 두 번째 요청이 필요한 확인을 see에서 뺐다. ReplyTest가 확인한다
- erd는 reply 장 끝 기준이다. reply_tb가 더해졌다
