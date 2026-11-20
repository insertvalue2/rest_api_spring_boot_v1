# admin-api ERD

```mermaid
erDiagram
  user_tb ||--o{ user_role_tb : ""
  user_tb ||--o{ board_tb : ""
  user_tb ||--o{ reply_tb : ""
  board_tb ||--o{ reply_tb : ""
  user_tb {
    integer id PK
    varchar username
    varchar password
    varchar email
    timestamp created_at
  }
  user_role_tb {
    integer user_id FK
    varchar role
  }
  board_tb {
    integer id PK
    varchar title
    varchar content
    integer user_id FK
    timestamp created_at
  }
  reply_tb {
    integer id PK
    varchar comment
    integer user_id FK
    integer board_id FK
    timestamp created_at
  }
```

이 장에서 생긴 것
- user_role_tb 테이블
