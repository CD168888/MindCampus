-- 移除冗余字段，改为通过 user_id 关联 sys_user 表查询

-- 评论表
ALTER TABLE `community_comment`
DROP COLUMN IF EXISTS `user_name`,
DROP COLUMN IF EXISTS `user_avatar`,
DROP COLUMN IF EXISTS `student_id`;

-- 帖子表
ALTER TABLE `community_post`
DROP COLUMN IF EXISTS `user_name`,
DROP COLUMN IF EXISTS `user_avatar`,
DROP COLUMN IF EXISTS `student_id`;
