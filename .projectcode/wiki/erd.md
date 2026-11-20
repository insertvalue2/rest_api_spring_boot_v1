# ERD

기준: board-list 장 끝

```mermaid
erDiagram
  user_tb ||--o{ board_tb : ""
  user_tb {
    integer id PK
    varchar username
    varchar password
    varchar email
    timestamp created_at
  }
  board_tb {
    integer id PK
    varchar title
    varchar content
    integer user_id FK
    timestamp created_at
  }
```
