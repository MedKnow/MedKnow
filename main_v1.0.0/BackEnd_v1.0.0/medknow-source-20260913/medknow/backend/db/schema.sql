-- ============================================================
-- 「药知道」MedKnow 数据库结构 (MySQL 8, utf8mb4)
-- 执行方式: mysql -u medknow -p medknow < schema.sql
-- 说明: 逻辑外键 + 索引，无物理外键约束
-- ============================================================

USE medknow;

-- 1. 用户表（含偏好设置字段）
CREATE TABLE IF NOT EXISTS `user` (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  phone               VARCHAR(11)  NOT NULL COMMENT '登录手机号',
  user_name           VARCHAR(20)  NOT NULL COMMENT '昵称',
  avatar              VARCHAR(255) NULL     COMMENT '头像URL',
  age                 INT          NULL     COMMENT '年龄 0-120',
  gender              VARCHAR(10)  NULL     COMMENT 'MALE/FEMALE/OTHER',
  occupation          VARCHAR(50)  NULL     COMMENT '职业',
  allergies           VARCHAR(255) NULL     COMMENT '过敏史，逗号分隔',
  chronic_diseases    VARCHAR(255) NULL     COMMENT '慢性病史，逗号分隔',
  status              VARCHAR(10)  NOT NULL DEFAULT 'PENDING' COMMENT 'NORMAL/PENDING/RESTRICTED',
  theme_color         VARCHAR(10)  NOT NULL DEFAULT 'PURPLE' COMMENT 'PURPLE/GREEN/BLUE/ORANGE/GRAY',
  notification_enabled TINYINT(1)  NOT NULL DEFAULT 1 COMMENT 'App推送开关',
  sms_reminder_enabled TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '短信提醒开关',
  created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_phone (phone),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 2. 短信验证码
CREATE TABLE IF NOT EXISTS sms_code (
  id            BIGINT      NOT NULL AUTO_INCREMENT,
  phone         VARCHAR(11) NOT NULL,
  code          VARCHAR(6)  NOT NULL COMMENT '开发模式明文存储',
  attempt_count INT         NOT NULL DEFAULT 0 COMMENT '错误次数，满5次锁定',
  expire_at     DATETIME    NOT NULL COMMENT '过期时间 = 发送时间+5分钟',
  created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用于60秒重发限制',
  PRIMARY KEY (id),
  KEY idx_phone_created (phone, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码';

-- 3. 用药计划
CREATE TABLE IF NOT EXISTS medication_plan (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  user_id          BIGINT       NOT NULL,
  status           VARCHAR(10)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/ACTIVE/PAUSED/COMPLETED/EXPIRED',
  start_date       DATE         NOT NULL,
  end_date         DATE         NOT NULL,
  notes            VARCHAR(500) NULL,
  diagnosis        VARCHAR(500) NULL COMMENT '诊断/医嘱',
  reminder_methods VARCHAR(50)  NOT NULL COMMENT '逗号分隔 ALARM,PUSH,SMS',
  conflicts        JSON         NULL COMMENT '智能解析冲突快照',
  activated_at     DATETIME     NULL,
  paused_at        DATETIME     NULL,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_status (user_id, status),
  KEY idx_user_start (user_id, start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用药计划';

-- 4. 计划内药品
CREATE TABLE IF NOT EXISTS plan_drug (
  id                   BIGINT       NOT NULL AUTO_INCREMENT,
  plan_id              BIGINT       NOT NULL,
  drug_id              BIGINT       NULL COMMENT '未匹配药品库时为NULL',
  drug_name            VARCHAR(100) NOT NULL COMMENT '用户输入原文',
  dosage               VARCHAR(50)  NOT NULL COMMENT '如"1片"',
  frequency            VARCHAR(50)  NOT NULL COMMENT '如"每日3次"',
  take_time            VARCHAR(100) NOT NULL COMMENT '逗号分隔 07:00,12:00,18:00',
  take_method          VARCHAR(20)  NOT NULL COMMENT 'BEFORE_MEAL/AFTER_MEAL/EMPTY_STOMACH/BEFORE_SLEEP',
  dietary_restrictions VARCHAR(255) NULL COMMENT '忌口',
  verified             TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否匹配药品库',
  dosage_risk          TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '剂量是否超说明书',
  sort_no              INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_plan (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='计划内药品';

-- 5. 提醒记录（打卡/统计的事实表）
CREATE TABLE IF NOT EXISTS reminder (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  plan_id        BIGINT       NOT NULL,
  user_id        BIGINT       NOT NULL COMMENT '冗余，统计免join',
  plan_drug_id   BIGINT       NOT NULL,
  drug_name      VARCHAR(100) NOT NULL COMMENT '冗余，列表免join',
  scheduled_date DATE         NOT NULL,
  scheduled_time TIME         NOT NULL,
  scheduled_at   DATETIME     NOT NULL COMMENT '状态机比较基准',
  status         VARCHAR(10)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/TRIGGERED/TAKEN/IGNORED/EXPIRED',
  actual_time    DATETIME     NULL COMMENT '打卡实际时间',
  is_late        TINYINT(1)   NOT NULL DEFAULT 0,
  created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_drug_time (plan_drug_id, scheduled_date, scheduled_time) COMMENT '激活幂等',
  KEY idx_user_date (user_id, scheduled_date),
  KEY idx_user_status (user_id, status),
  KEY idx_scheduled (scheduled_at, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服药提醒';

-- 6. 药品库
CREATE TABLE IF NOT EXISTS drug (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  drug_name       VARCHAR(100)  NOT NULL COMMENT '商品名',
  generic_name    VARCHAR(100)  NULL COMMENT '通用名',
  category        VARCHAR(50)   NULL COMMENT '抗生素/解热镇痛/...',
  summary         VARCHAR(500)  NULL COMMENT '搜索列表摘要',
  indications     TEXT          NULL COMMENT '适应症',
  dosage          TEXT          NULL COMMENT '用法用量',
  side_effects    TEXT          NULL COMMENT '不良反应',
  contraindications TEXT        NULL COMMENT '禁忌',
  precautions     TEXT          NULL COMMENT '注意事项',
  storage         VARCHAR(255)  NULL COMMENT '存储条件',
  max_single_dose DECIMAL(10,2) NULL COMMENT '说明书最大单次剂量数值',
  max_dose_unit   VARCHAR(10)   NULL COMMENT '对应单位 mg/g/ml/片/粒/支',
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_name (drug_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品库';

-- 7. 药箱收藏
CREATE TABLE IF NOT EXISTS user_drug_box (
  id         BIGINT   NOT NULL AUTO_INCREMENT,
  user_id    BIGINT   NOT NULL,
  drug_id    BIGINT   NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_drug (user_id, drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药箱收藏';

-- 8. 医院
CREATE TABLE IF NOT EXISTS hospital (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  name         VARCHAR(100)  NOT NULL,
  address      VARCHAR(255)  NOT NULL,
  phone        VARCHAR(20)   NULL,
  rating       DECIMAL(2,1)  NOT NULL DEFAULT 4.5 COMMENT '口碑分 0-5',
  level        VARCHAR(20)   NULL COMMENT '三甲/二甲/社区',
  longitude    DECIMAL(10,6) NOT NULL,
  latitude     DECIMAL(10,6) NOT NULL,
  introduction TEXT          NULL,
  created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院';

-- 9. 医院科室
CREATE TABLE IF NOT EXISTS hospital_department (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  hospital_id     BIGINT       NOT NULL,
  department_name VARCHAR(50)  NOT NULL,
  description     VARCHAR(500) NULL,
  is_main         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1=强项科室',
  PRIMARY KEY (id),
  KEY idx_hospital (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医院科室';

-- 10. 科普文章
CREATE TABLE IF NOT EXISTS article (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  title        VARCHAR(100) NOT NULL,
  summary      VARCHAR(200) NOT NULL,
  content      MEDIUMTEXT   NOT NULL,
  cover_image  VARCHAR(255) NULL,
  category     VARCHAR(10)  NOT NULL COMMENT '中药/西药/养生/疾病/饮食',
  author       VARCHAR(50)  NOT NULL,
  author_title VARCHAR(100) NULL,
  tags         VARCHAR(255) NULL COMMENT '逗号分隔',
  read_count   INT          NOT NULL DEFAULT 0,
  like_count   INT          NOT NULL DEFAULT 0,
  collect_count INT         NOT NULL DEFAULT 0,
  share_count  INT          NOT NULL DEFAULT 0,
  status       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1=已发布',
  publish_time DATETIME     NOT NULL,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_category_pub (category, publish_time),
  KEY idx_pub (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科普文章';

-- 11. 文章点赞/收藏记录（切换式）
CREATE TABLE IF NOT EXISTS article_interaction (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  user_id    BIGINT      NOT NULL,
  article_id BIGINT      NOT NULL,
  type       VARCHAR(10) NOT NULL COMMENT 'LIKE/COLLECT',
  created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_article_type (user_id, article_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章互动';

-- 12. 文章浏览行为（喂推荐算法）
CREATE TABLE IF NOT EXISTS article_behavior (
  id            BIGINT      NOT NULL AUTO_INCREMENT,
  user_id       BIGINT      NULL COMMENT '未登录可空',
  article_id    BIGINT      NOT NULL,
  behavior_type VARCHAR(10) NOT NULL COMMENT 'VIEW/LIKE/COLLECT/SHARE',
  target        VARCHAR(10) NULL COMMENT '分享平台 WECHAT/QQ/COPY_LINK',
  created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_time (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章行为';

-- 13. 用户反馈
CREATE TABLE IF NOT EXISTS feedback (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_id    BIGINT       NULL COMMENT '匿名可提交',
  content    VARCHAR(500) NOT NULL,
  contact    VARCHAR(100) NULL,
  images     VARCHAR(500) NULL COMMENT '逗号分隔URL，最多3张',
  status     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '0=未处理',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户反馈';
