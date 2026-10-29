-- 认证链路最小种子数据
-- 默认账号：admin / 123456（BCrypt，由 sa-token BCrypt.hashpw 生成）
-- 可重复执行，已存在的行会跳过

INSERT IGNORE INTO sys_dept (id, name, parent_id, tree_path, sort, status, deleted, create_by, update_by)
VALUES (1, 'Flux 科技', 0, '0', 1, 1, 0, 1, 1);

INSERT IGNORE INTO sys_role (id, name, code, sort, status, data_scope, deleted, create_by, update_by)
VALUES (1, '超级管理员', 'ROOT', 1, 1, 0, 0, 1, 1);

INSERT IGNORE INTO sys_user (id, username, nickname, gender, password, dept_id, status, deleted, create_by, update_by)
VALUES (1, 'admin', '超级管理员', 1, '$2a$10$6/PWnny5Scb1qxGSeiyhFOgdcG6SdsSxhMECizU6uMvka7CwX5D2m', 1, 1, 0, 1, 1);

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);
