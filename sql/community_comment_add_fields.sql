-- 为社区评论表添加回复功能所需的字段（如字段已存在则忽略）
ALTER TABLE `community_comment`
ADD COLUMN IF NOT EXISTS `parent_id` bigint DEFAULT NULL COMMENT '父评论ID（用于实现评论回复）' AFTER `content`,
ADD COLUMN IF NOT EXISTS `reply_to_user_id` bigint DEFAULT NULL COMMENT '回复的目标用户ID' AFTER `parent_id`,
ADD COLUMN IF NOT EXISTS `reply_to_user_name` varchar(100) DEFAULT NULL COMMENT '回复的目标用户名' AFTER `reply_to_user_id`;

-- 添加索引（如索引已存在则忽略）
ALTER TABLE `community_comment`
ADD INDEX IF NOT EXISTS `idx_parent_id` (`parent_id`),
ADD INDEX IF NOT EXISTS `idx_post_id_comment` (`post_id`);
