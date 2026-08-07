-- ============================================================
-- 大学生创新创业大赛平台 - 数据库初始化脚本
-- MySQL 8.0
-- 字符集: utf8mb4
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. sys_user 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`        VARCHAR(50)   NOT NULL                COMMENT '学号/用户名',
    `password`        VARCHAR(200)  NOT NULL                COMMENT '密码(BCrypt加密)',
    `name`            VARCHAR(50)   NOT NULL                COMMENT '真实姓名',
    `openid`          VARCHAR(100)  DEFAULT NULL            COMMENT '微信openid',
    `phone`           VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `email`           VARCHAR(100)  DEFAULT NULL            COMMENT '邮箱',
    `role`            ENUM('student','expert','admin') NOT NULL COMMENT '角色: 学生/专家/管理员',
    `college`         VARCHAR(100)  DEFAULT NULL            COMMENT '所在学院',
    `major`           VARCHAR(100)  DEFAULT NULL            COMMENT '专业',
    `grade`           VARCHAR(20)   DEFAULT NULL            COMMENT '年级',
    `expert_title`    VARCHAR(50)   DEFAULT NULL            COMMENT '专家职称',
    `expert_field`    VARCHAR(200)  DEFAULT NULL            COMMENT '评审领域(逗号分隔)',
    `status`          TINYINT       NOT NULL DEFAULT 1      COMMENT '状态: 1启用 0禁用',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_role` (`role`),
    KEY `idx_college` (`college`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. competition 大赛表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`           VARCHAR(200)  NOT NULL                COMMENT '大赛标题',
    `description`     TEXT          DEFAULT NULL            COMMENT '大赛描述',
    `cover_image`     VARCHAR(500)  DEFAULT NULL            COMMENT '封面图片URL',
    `start_time`      DATETIME      NOT NULL                COMMENT '大赛开始时间',
    `end_time`        DATETIME      NOT NULL                COMMENT '大赛结束时间',
    `register_start`  DATETIME      NOT NULL                COMMENT '报名开始时间',
    `register_end`    DATETIME      NOT NULL                COMMENT '报名截止时间',
    `category`        VARCHAR(50)   DEFAULT NULL            COMMENT '赛道分类: 互联网+/智能制造/生物医药/农业食品/文化创意/其他',
    `status`          ENUM('draft','published','judging','finished') NOT NULL DEFAULT 'draft' COMMENT '状态: 草稿/已发布/评审中/已结束',
    `created_by`      BIGINT        NOT NULL                COMMENT '创建人(管理员ID)',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='大赛表';

-- ------------------------------------------------------------
-- 3. project 项目申报表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `competition_id`  BIGINT        NOT NULL                COMMENT '所属大赛ID',
    `title`           VARCHAR(200)  NOT NULL                COMMENT '项目名称',
    `team_name`       VARCHAR(100)  DEFAULT NULL            COMMENT '团队名称',
    `leader_id`       BIGINT        NOT NULL                COMMENT '负责人用户ID',
    `leader_name`     VARCHAR(50)   NOT NULL                COMMENT '负责人姓名',
    `leader_phone`    VARCHAR(20)   DEFAULT NULL            COMMENT '负责人联系电话',
    `team_members`    VARCHAR(500)  DEFAULT NULL            COMMENT '团队成员(逗号分隔姓名)',
    `college`         VARCHAR(100)  DEFAULT NULL            COMMENT '所属学院',
    `category`        VARCHAR(50)   DEFAULT NULL            COMMENT '项目赛道',
    `abstract`        TEXT          DEFAULT NULL            COMMENT '项目摘要',
    `plan_content`    TEXT          DEFAULT NULL            COMMENT '方案内容',
    `innovation_points` TEXT        DEFAULT NULL            COMMENT '创新点',
    `expected_results` TEXT          DEFAULT NULL            COMMENT '预期成果',
    `attachments`     VARCHAR(2000) DEFAULT NULL            COMMENT '附件(JSON数组字符串)',
    `status`          ENUM('draft','submitted','reviewing','approved','rejected') NOT NULL DEFAULT 'draft' COMMENT '状态: 草稿/已提交/评审中/已通过/已驳回',
    `submit_time`     DATETIME      DEFAULT NULL            COMMENT '提交时间',
    `review_time`     DATETIME      DEFAULT NULL            COMMENT '评审完成时间',
    `reviewer_comment` TEXT         DEFAULT NULL            COMMENT '评审总体意见',
    `final_score`     DECIMAL(5,2)  DEFAULT NULL            COMMENT '最终综合得分',
    `prize`           VARCHAR(50)   DEFAULT NULL            COMMENT '获奖等级',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_competition_leader` (`competition_id`, `leader_id`) COMMENT '同一大赛同一负责人不可重复提交',
    KEY `idx_leader_id` (`leader_id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`),
    CONSTRAINT `fk_project_competition` FOREIGN KEY (`competition_id`) REFERENCES `competition`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_project_leader`      FOREIGN KEY (`leader_id`)      REFERENCES `sys_user`(`id`)      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目申报表';

-- ------------------------------------------------------------
-- 4. review 评审打分表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `project_id`         BIGINT       NOT NULL                COMMENT '项目ID',
    `expert_id`          BIGINT       NOT NULL                COMMENT '评审专家ID',
    `score_innovation`   DECIMAL(4,2) DEFAULT NULL            COMMENT '创新性评分(0-10)',
    `score_feasibility`  DECIMAL(4,2) DEFAULT NULL            COMMENT '可行性评分(0-10)',
    `score_team`         DECIMAL(4,2) DEFAULT NULL            COMMENT '团队评分(0-10)',
    `score_presentation` DECIMAL(4,2) DEFAULT NULL            COMMENT '展示评分(0-10)',
    `score_total`        DECIMAL(5,2) DEFAULT NULL            COMMENT '综合总分(0-100)',
    `comment`            TEXT         DEFAULT NULL            COMMENT '评审意见',
    `score_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打分时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_expert` (`project_id`, `expert_id`) COMMENT '同一专家对同一项目只能评审一次',
    KEY `idx_expert_id` (`expert_id`),
    CONSTRAINT `fk_review_project` FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_expert` FOREIGN KEY (`expert_id`)  REFERENCES `sys_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评审打分表';

-- ------------------------------------------------------------
-- 5. announcement 公告表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`        VARCHAR(200) NOT NULL                COMMENT '公告标题',
    `content`      TEXT         DEFAULT NULL            COMMENT '公告内容',
    `category`     VARCHAR(50)  DEFAULT NULL            COMMENT '分类: 通知/公告/赛事',
    `published_at` DATETIME     DEFAULT NULL            COMMENT '发布时间',
    `publisher_id` BIGINT       NOT NULL                COMMENT '发布人ID',
    `top`          TINYINT      NOT NULL DEFAULT 0      COMMENT '是否置顶: 1置顶 0否',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_top` (`top`),
    KEY `idx_publisher` (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ------------------------------------------------------------
-- 6. file_upload 文件上传记录表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `file_upload`;
CREATE TABLE `file_upload` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `original_name` VARCHAR(200) NOT NULL                COMMENT '原始文件名',
    `stored_name`   VARCHAR(200) NOT NULL                COMMENT '存储文件名',
    `file_path`     VARCHAR(500) NOT NULL                COMMENT '文件存储路径',
    `file_size`     BIGINT       DEFAULT NULL            COMMENT '文件大小(字节)',
    `content_type`  VARCHAR(100) DEFAULT NULL            COMMENT 'MIME类型',
    `uploader_id`   BIGINT       NOT NULL                COMMENT '上传人ID',
    `biz_type`      VARCHAR(50)  DEFAULT NULL            COMMENT '业务类型: project/avatar/attachment/competition',
    `biz_id`        BIGINT       DEFAULT NULL            COMMENT '业务关联ID',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    KEY `idx_uploader` (`uploader_id`),
    KEY `idx_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- ============================================================
-- 种子数据
-- 所有账号统一密码: 123456
-- BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================

-- ------------------------------------------------------------
-- sys_user 种子数据: 2管理员 + 3专家 + 5学生
-- ------------------------------------------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `name`, `openid`, `phone`, `email`, `role`, `college`, `major`, `grade`, `expert_title`, `expert_field`, `status`) VALUES
-- 管理员
(1,  'admin',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', NULL, '13800000001', 'admin@demo.edu.cn',       'admin',  NULL,             NULL,         NULL,     NULL,        NULL,                                       1),
(2,  'admin02',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李老师',     NULL, '13800000002', 'admin02@demo.edu.cn',     'admin',  NULL,             NULL,         NULL,     NULL,        NULL,                                       1),
-- 专家
(3,  'expert1',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王专家',     NULL, '13900000001', 'expert1@demo.edu.cn',     'expert', NULL,             NULL,         NULL,     '教授',      '互联网+,信息技术,人工智能',                  1),
(4,  'expert2',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张专家',     NULL, '13900000002', 'expert2@demo.edu.cn',     'expert', NULL,             NULL,         NULL,     '副教授',    '智能制造,机械工程,工业设计',                1),
(5,  'expert3',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵专家',     NULL, '13900000003', 'expert3@demo.edu.cn',     'expert', NULL,             NULL,         NULL,     '研究员',    '生物医药,生物技术,医疗器械',                1),
-- 学生
(6,  'student01',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '陈小明',     'o0xxxxstudent01', '13700000001', 'student01@demo.edu.cn',   'student','计算机学院',     '软件工程',   '2023级', NULL,        NULL,                                       1),
(7,  'student02',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '林小红',     'o0xxxxstudent02', '13700000002', 'student02@demo.edu.cn',   'student','计算机学院',     '人工智能',   '2023级', NULL,        NULL,                                       1),
(8,  'student03',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '黄大鹏',     'o0xxxxstudent03', '13700000003', 'student03@demo.edu.cn',   'student','机械工程学院',   '机械设计',   '2022级', NULL,        NULL,                                       1),
(9,  'student04',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '刘小美',     'o0xxxxstudent04', '13700000004', 'student04@demo.edu.cn',   'student','生命科学学院',   '生物技术',   '2023级', NULL,        NULL,                                       1),
(10, 'student05',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周小强',     'o0xxxxstudent05', '13700000005', 'student05@demo.edu.cn',   'student','经济管理学院',   '工商管理',   '2022级', NULL,        NULL,                                       1);

-- ------------------------------------------------------------
-- competition 种子数据: 3个大赛, 覆盖不同赛道
-- ------------------------------------------------------------
INSERT INTO `competition` (`id`, `title`, `description`, `cover_image`, `start_time`, `end_time`, `register_start`, `register_end`, `category`, `status`, `created_by`) VALUES
(1, '第九届"互联网+"大学生创新创业大赛校内选拔赛',
     '深入贯彻落实党中央、国务院关于高校毕业生就业创业工作的部署要求，聚焦战略性新兴产业发展方向，引导高校师生投身创新创业实践，推动科技成果转化，培养高素质创新创业人才。',
     '/uploads/competition/cover_internet_plus.jpg',
     '2026-09-01 09:00:00', '2026-12-31 18:00:00',
     '2026-08-15 09:00:00', '2026-09-15 23:59:59',
     '互联网+', 'published', 1),
(2, '第五届智能制造创新设计大赛',
     '面向制造业转型升级需求，聚焦智能装备、机器人、工业互联网等前沿领域，鼓励大学生开展创新性设计与实践，推动智能制造技术的应用与推广。',
     '/uploads/competition/cover_smart_mfg.jpg',
     '2026-10-10 09:00:00', '2027-01-31 18:00:00',
     '2026-09-20 09:00:00', '2026-10-20 23:59:59',
     '智能制造', 'published', 1),
(3, '首届生物医药创新创业挑战赛',
     '聚焦生物医药前沿领域，鼓励大学生开展药物研发、医疗器械、生物技术等方面的创新研究，培育生物医药领域未来创新创业人才。',
     '/uploads/competition/cover_biomed.jpg',
     '2026-07-01 09:00:00', '2026-11-30 18:00:00',
     '2026-06-15 09:00:00', '2026-07-15 23:59:59',
     '生物医药', 'judging', 1);

-- ------------------------------------------------------------
-- project 种子数据: 7个项目, 状态多样
-- ------------------------------------------------------------
INSERT INTO `project` (`id`, `competition_id`, `title`, `team_name`, `leader_id`, `leader_name`, `leader_phone`, `team_members`, `college`, `category`, `abstract`, `plan_content`, `innovation_points`, `expected_results`, `attachments`, `status`, `submit_time`, `final_score`, `prize`, `reviewer_comment`, `review_time`) VALUES
-- 互联网+ 大赛 (competition_id = 1)
(1, 1, '智学AI自适应学习平台', '智学堂团队', 6, '陈小明', '13700000001', '陈小明,林小红,周小强', '计算机学院', '互联网+',
     '基于大语言模型和知识图谱的大学生个性化学习辅助平台，提供智能答疑、习题推荐、错题分析等功能。',
     '1. 需求分析与用户画像构建\n2. 知识图谱自动构建与更新\n3. LLM驱动的智能答疑引擎\n4. 自适应学习路径推荐算法\n5. 前后端系统架构设计与实现\n6. 小范围试点与效果评估',
     '1. 多模态知识图谱自动构建技术\n2. 融合LLM与传统NLP的智能答疑\n3. 个性化学习路径自适应推荐',
     '完成原型系统开发，校内500名用户试点，学习效率提升30%以上，申请软件著作权2项。',
     '["/uploads/attachments/proposal_01.pdf","/uploads/attachments/ppt_01.pdf"]',
     'submitted', '2026-08-20 14:30:00', NULL, NULL, NULL, NULL),

(2, 1, '云端笔记协同工作台', '云笺团队', 7, '林小红', '13700000002', '林小红,陈小明', '计算机学院', '互联网+',
     '一款面向大学生的轻量级云端笔记工具，支持多人实时协同编辑、Markdown渲染、思维导图等功能。',
     '采用React + Node.js技术栈，基于WebSocket实现实时协同，使用CRDT算法解决冲突合并问题，支持端到端加密存储。',
     '1. 基于CRDT的无冲突协同编辑\n2. 增量式加密存储方案\n3. 插件化扩展架构',
     '上线公测版本，注册用户突破2000人，成为校园内主流笔记工具。',
     '["/uploads/attachments/proposal_02.pdf"]',
     'approved', '2026-08-22 10:00:00', 82.50, '二等奖', '整体方案完整，技术选型合理，建议加强市场推广部分。', '2026-08-28 16:00:00'),

(3, 1, '校园二手书共享平台', '书香校园', 10, '周小强', '13700000005', '周小强,黄大鹏', '经济管理学院', '互联网+',
     '依托微信小程序搭建校园二手书流转平台，支持书籍发布、交易、捐赠和书评功能。',
     '采用Spring Boot + UniApp技术方案，引入信用评分机制保障交易安全，结合校园物流降低交付成本。',
     '1. 校园场景化信用体系\n2. OCR自动识别书籍信息\n3. 书籍流向追溯系统',
     '服务覆盖全校80%以上在校生，年交易量突破5000册，打造绿色校园文化品牌。',
     '["/uploads/attachments/proposal_03.pdf","/uploads/attachments/budget_03.xlsx"]',
     'reviewing', '2026-08-25 09:15:00', NULL, NULL, NULL, NULL),

-- 智能制造大赛 (competition_id = 2)
(4, 2, '柔性抓取机器人末端执行器设计', '机械先锋', 8, '黄大鹏', '13700000003', '黄大鹏,陈小明', '机械工程学院', '智能制造',
     '设计一款基于形状记忆合金的柔性末端执行器，可自适应抓取不同形状、材质的物体。',
     '1. 形状记忆合金驱动单元设计与仿真\n2. 仿生柔性手指结构设计\n3. 力传感器集成与控制算法\n4. 原型机加工与装配\n5. 抓取性能测试与迭代优化',
     '1. 仿生结构与SMA驱动融合\n2. 一体化触觉传感方案\n3. 多目标自适应抓取规划',
     '完成原理样机试制，实现不少于10种典型物体的可靠抓取，申请发明专利1项。',
     '["/uploads/attachments/proposal_04.pdf","/uploads/attachments/cad_04.zip"]',
     'draft', NULL, NULL, NULL, NULL, NULL),

(5, 2, '工业现场多传感器融合监测系统', '智感团队', 6, '陈小明', '13700000001', '陈小明,黄大鹏', '计算机学院', '智能制造',
     '面向离散制造车间的多源异构数据采集与融合监测系统，实现设备状态的实时感知与预测性维护。',
     '边缘网关 + 云平台架构，边缘侧完成数据预处理与特征提取，云端进行深度学习建模与故障预测。',
     '1. 面向工业场景的边缘计算优化\n2. 多模态传感器数据融合算法\n3. 小样本下的故障诊断模型',
     '在合作企业生产线上试点部署，设备非计划停机时间减少20%以上。',
     '["/uploads/attachments/proposal_05.pdf"]',
     'submitted', '2026-09-25 11:00:00', NULL, NULL, NULL, NULL),

-- 生物医药挑战赛 (competition_id = 3, 正在评审中)
(6, 3, '基于微流控芯片的快速核酸检测平台', '微检团队', 9, '刘小美', '13700000004', '刘小美,林小红', '生命科学学院', '生物医药',
     '开发一款便携式微流控核酸检测芯片，样本即进即出，30分钟内可完成核酸检测全流程。',
     '1. 微流控芯片结构设计与加工\n2. 片上样品前处理与扩增方案\n3. 荧光检测模块集成\n4. 配套试剂配方优化\n5. 性能验证与临床试验',
     '1. 全集成片上实验室(LoC)方案\n2. 低功耗恒温扩增技术\n3. 智能手机辅助读出方式',
     '完成工程化样机开发，通过性能验证，申请发明专利2项，进入医疗注册申报流程。',
     '["/uploads/attachments/proposal_06.pdf","/uploads/attachments/ethics_06.pdf"]',
     'approved', '2026-07-10 15:00:00', 91.25, '一等奖', '创新性强，产业化前景好，建议尽快推进临床验证。', '2026-07-20 10:00:00'),

(7, 3, '新型肿瘤免疫靶点的筛选与验证', '免疫探索队', 9, '刘小美', '13700000004', '刘小美', '生命科学学院', '生物医药',
     '通过单细胞测序结合生物信息学分析，筛选肿瘤微环境中的新型免疫治疗靶点，并进行初步功能验证。',
     '1. 肿瘤样本收集与单细胞测序\n2. 差异表达基因与通路富集分析\n3. 候选靶点体外功能验证\n4. 动物模型体内评价',
     '1. 多组学联合靶点筛选策略\n2. 类器官模型功能验证体系',
     '鉴定1-2个具有临床转化潜力的靶点，发表SCI论文1篇，申请发明专利1项。',
     '["/uploads/attachments/proposal_07.pdf"]',
     'rejected', '2026-07-12 09:30:00', 58.00, NULL, '项目创新性不足，研究方案不够清晰，建议补充前期工作基础后重新申报。', '2026-07-18 14:00:00');

-- ------------------------------------------------------------
-- review 种子数据: 为已完成评审的项目打分
-- ------------------------------------------------------------
INSERT INTO `review` (`project_id`, `expert_id`, `score_innovation`, `score_feasibility`, `score_team`, `score_presentation`, `score_total`, `comment`, `score_time`) VALUES
-- 项目2: 云端笔记协同工作台 (已通过)
(2, 3, 8.50, 8.00, 8.00, 8.00, 81.50, '协同编辑技术方案较成熟，建议加强差异化竞争优势的阐述。', '2026-08-26 10:00:00'),
(2, 4, 8.00, 8.50, 8.50, 8.00, 82.50, '团队能力较强，整体方案可行，给予通过。',              '2026-08-27 09:30:00'),

-- 项目6: 微流控核酸检测平台 (一等奖)
(6, 3, 9.00, 8.50, 9.00, 9.00, 89.50, '方案设计新颖，工程化路径清晰，值得重点支持。', '2026-07-15 14:00:00'),
(6, 4, 9.50, 9.00, 9.00, 9.00, 92.00, '技术创新性突出，产业化潜力巨大，推荐为一等奖。', '2026-07-16 11:00:00'),
(6, 5, 9.00, 9.00, 9.50, 9.00, 92.25, '生物医药方向兼具创新与落地价值，团队背景优秀。',  '2026-07-17 09:00:00'),

-- 项目7: 肿瘤免疫靶点 (已驳回)
(7, 5, 6.00, 6.00, 5.50, 5.50, 58.00, '项目创新性不足，方案缺乏关键技术路线，建议补充前期工作。', '2026-07-15 16:00:00'),

-- 项目1: 智学AI平台 (评审中, 已打分一位专家)
(1, 3, 8.50, 7.50, 8.00, 8.00, 80.00, '大模型结合教育场景是不错的切入点，需关注数据安全合规问题。', '2026-08-29 10:30:00');

-- ------------------------------------------------------------
-- announcement 种子数据
-- ------------------------------------------------------------
INSERT INTO `announcement` (`id`, `title`, `content`, `category`, `published_at`, `publisher_id`, `top`) VALUES
(1, '关于举办第九届"互联网+"大学生创新创业大赛的通知',
     '各学院、各相关单位：\n\n为深入贯彻落实党中央、国务院关于高校毕业生就业创业工作的部署要求，学校决定举办第九届"互联网+"大学生创新创业大赛校内选拔赛。现将有关事项通知如下：\n\n一、参赛对象：全体在校本科生、研究生\n二、报名时间：2026年8月15日-9月15日\n三、比赛赛道：主赛道、青年红色筑梦之旅赛道、高教主赛道\n四、奖项设置：金奖10项、银奖20项、铜奖30项\n\n请各学院认真组织，广泛动员，确保本次大赛顺利开展。',
     '通知', '2026-08-01 09:00:00', 1, 1),

(2, '平台上线公告',
     '各位老师、同学：\n\n大学生创新创业大赛管理平台今日正式上线！平台支持赛事报名、项目提交、专家评审、结果公示等全流程在线办理。\n\n主要功能：\n1. 赛事浏览与在线报名\n2. 项目材料在线提交\n3. 专家在线评审打分\n4. 获奖结果公示\n\n如有使用问题，请联系创新创业中心。',
     '公告', '2026-07-15 10:00:00', 1, 0),

(3, '首届生物医药创新创业挑战赛评审专家名单公示',
     '首届生物医药创新创业挑战赛校内评审即将启动，现将评审专家名单公示如下：\n\n1. 王XX 教授 (计算机学院)\n2. 张XX 副教授 (机械工程学院)\n3. 赵XX 研究员 (生命科学学院)\n\n公示期：2026年7月20日-7月22日\n\n如有异议，请在公示期内通过邮件联系我们。',
     '公告', '2026-07-18 15:30:00', 2, 0),

(4, '第五届智能制造创新设计大赛报名指南',
     '一、注册登录\n使用学号/工号进行注册，完善个人信息后即可报名参赛。\n\n二、创建团队\n每个项目团队成员不超过5人，设负责人1名。\n\n三、提交材料\n项目申报书（PDF）、项目展示PPT（PDF）、团队成员签字扫描件。\n\n四、注意事项\n请在截止时间前完成所有材料提交，逾期系统将自动关闭报名通道。',
     '赛事', '2026-09-10 11:00:00', 1, 0);

-- ------------------------------------------------------------
-- file_upload 种子数据
-- ------------------------------------------------------------
INSERT INTO `file_upload` (`id`, `original_name`, `stored_name`, `file_path`, `file_size`, `content_type`, `uploader_id`, `biz_type`, `biz_id`) VALUES
(1, 'proposal_01.pdf',    'a1b2c3d4e5f6.pdf',    '/uploads/attachments/a1b2c3d4e5f6.pdf',    2580480, 'application/pdf',  6,  'project', 1),
(2, 'ppt_01.pdf',         'b2c3d4e5f6a7.pdf',    '/uploads/attachments/b2c3d4e5f6a7.pdf',    5120000, 'application/pdf',  6,  'project', 1),
(3, 'proposal_02.pdf',    'c3d4e5f6a7b8.pdf',    '/uploads/attachments/c3d4e5f6a7b8.pdf',    1843200, 'application/pdf',  7,  'project', 2),
(4, 'proposal_03.pdf',    'd4e5f6a7b8c9.pdf',    '/uploads/attachments/d4e5f6a7b8c9.pdf',    2097152, 'application/pdf',  10, 'project', 3),
(5, 'budget_03.xlsx',     'e5f6a7b8c9d0.xlsx',   '/uploads/attachments/e5f6a7b8c9d0.xlsx',    65536,  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 10, 'project', 3),
(6, 'proposal_04.pdf',    'f6a7b8c9d0e1.pdf',    '/uploads/attachments/f6a7b8c9d0e1.pdf',    2359296, 'application/pdf',  8,  'project', 4),
(7, 'cad_04.zip',         'a7b8c9d0e1f2.zip',    '/uploads/attachments/a7b8c9d0e1f2.zip',    8388608, 'application/zip',  8,  'project', 4),
(8, 'proposal_05.pdf',    'b8c9d0e1f2a3.pdf',    '/uploads/attachments/b8c9d0e1f2a3.pdf',    1966080, 'application/pdf',  6,  'project', 5),
(9, 'proposal_06.pdf',    'c9d0e1f2a3b4.pdf',    '/uploads/attachments/c9d0e1f2a3b4.pdf',    3145728, 'application/pdf',  9,  'project', 6),
(10,'ethics_06.pdf',      'd0e1f2a3b4c5.pdf',    '/uploads/attachments/d0e1f2a3b4c5.pdf',    524288,  'application/pdf',  9,  'project', 6),
(11,'proposal_07.pdf',    'e1f2a3b4c5d6.pdf',    '/uploads/attachments/e1f2a3b4c5d6.pdf',    1572864, 'application/pdf',  9,  'project', 7),
(12,'cover_internet_plus.jpg',   'img01.jpg', '/uploads/competition/img01.jpg',   89478,  'image/jpeg', 1, 'competition', 1),
(13,'cover_smart_mfg.jpg',       'img02.jpg', '/uploads/competition/img02.jpg',   102400, 'image/jpeg', 1, 'competition', 2),
(14,'cover_biomed.jpg',          'img03.jpg', '/uploads/competition/img03.jpg',   87312,  'image/jpeg', 1, 'competition', 3);

SET FOREIGN_KEY_CHECKS = 1;
