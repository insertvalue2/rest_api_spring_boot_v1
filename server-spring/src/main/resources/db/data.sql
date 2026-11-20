insert into user_tb (username, password, email, created_at) values ('ssar', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'ssar@metacoding.com', now());
insert into user_tb (username, password, email, created_at) values ('cos', '$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO', 'cos@metacoding.com', now());

insert into board_tb (title, content, user_id, created_at) values ('제목1', '내용1', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목2', '내용2', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목3', '내용3', 1, now());
insert into board_tb (title, content, user_id, created_at) values ('제목4', '내용4', 2, now());
insert into board_tb (title, content, user_id, created_at) values ('제목5', '내용5', 2, now());

insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글1', 4, 2, now());
insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글2', 4, 2, now());
insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글3', 4, 2, now());
insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글4', 5, 1, now());
insert into reply_tb (comment, board_id, user_id, created_at) values ('댓글5', 5, 1, now());
