package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_open_app_config.groovy') {
    changeSet(author: "changping.shi@hand-china.com", id: "2021-08-31_oauth_open_app_sync_config") {
        createTable(tableName: "oauth_open_app_config", remarks: "第三方应用配置表") {
            column(name: "id", type: "BIGINT UNSIGNED", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }

            column(name: 'open_app_id', type: 'BIGINT UNSIGNED', remarks: 'oauth_open_app 主键') {
                constraints(nullable: false)
            }
            column(name: 'login_name_field', type: 'varchar(20)', remarks: '登录名映射字段') { constraints(nullable: false) }
            column(name: 'real_name_field', type: 'varchar(20)', remarks: '真实姓名映射字段') { constraints(nullable: false) }
            column(name: 'email_field', type: 'varchar(20)', remarks: '邮箱号码映射字段') { constraints(nullable: false) }
            column(name: 'phone_field', type: 'varchar(20)', remarks: '手机号码映射字段') { constraints(nullable: false) }
            column(name: "timing_flag", type: 'smallint', defaultValue: '0', remarks: '是否启用定时同步') {
                constraints(nullable: false)
            }
            column(name: 'start_sync_time', type: 'datetime', remarks: '开始同步时间')
            column(name: 'frequency', type: 'varchar(20)', remarks: '同步频率,值集HIAM.LDAP_SYNC_FREQUENCY')
            column(name: 'quartz_task_id', type: 'BIGINT UNSIGNED', remarks: '定时任务 主键')

            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "更新时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
        }
        addUniqueConstraint(columnNames: "open_app_id", tableName: "oauth_open_app_config", constraintName: "oauth_open_app_config_uk1")
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-03-28_add_agile_column') {
        addColumn(tableName: 'OAUTH_OPEN_APP_CONFIG') {
            column(name: "agile_flag", type: 'smallint', defaultValue: '0', remarks: '是否启用同步工作项') {
                constraints(nullable: false)
            }
            column(name: "agile_priority_json", type: 'varchar(200)', remarks: '工作项优先级与钉钉映射关系')
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-04-26_add_agile_column') {
        addColumn(tableName: 'OAUTH_OPEN_APP_CONFIG') {
            column(name: "work_group_flag", type: 'smallint', defaultValue: '0', remarks: '是否启用同步通讯录架构到工作组') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-04-30_cron') {
        addColumn(tableName: 'OAUTH_OPEN_APP_CONFIG') {
            column(name: "CRON_EXPRESSION", type: 'varchar(50)', remarks: 'cron表达式')
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-05-10_add_agile_column') {
        addColumn(tableName: 'OAUTH_OPEN_APP_CONFIG') {
            column(name: "user_flag", type: 'smallint', defaultValue: '1', remarks: '是否同步用户') {
                constraints(nullable: false)
            }
            column(name: "message_flag", type: 'smallint', defaultValue: '0', remarks: '是否开启消息发送') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-06-09_add_yq_column') {
        addColumn(tableName: 'OAUTH_OPEN_APP_CONFIG') {
            column(name: "c7n_attribute", type: 'varchar(30)', remarks: 'c7n用户属性')
            column(name: "yq_attribute", type: 'varchar(30)', remarks: '燕千云用户属性')
            column(name: "all_scope", type: 'smallint', defaultValue: '0', remarks: '规则应用范围是否全量')
        }
    }

   //  用groovy语法去掉非空约束 该字段会没有备注
    changeSet(author: 'changping.shi@hand-china.com', id: '2022-06-10_modify_column') {
        sql("ALTER TABLE `hzero_platform`.`oauth_open_app_config` \n" +
            "MODIFY COLUMN `login_name_field` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '登录名映射字段' AFTER `open_app_id`,\n" +
            "MODIFY COLUMN `real_name_field` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '真实姓名映射字段' AFTER `login_name_field`,\n" +
            "MODIFY COLUMN `email_field` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '邮箱号码映射字段' AFTER `real_name_field`,\n" +
            "MODIFY COLUMN `phone_field` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '手机号码映射字段' AFTER `email_field`,\n" +
            "MODIFY COLUMN `agile_flag` smallint(6) NULL DEFAULT 0 COMMENT '是否启用同步工作项' AFTER `LAST_UPDATED_BY`,\n" +
            "MODIFY COLUMN `work_group_flag` smallint(6) NULL DEFAULT 0 COMMENT '是否启用同步通讯录架构到工作组' AFTER `CRON_EXPRESSION`,\n" +
            "MODIFY COLUMN `user_flag` smallint(6) NULL DEFAULT 1 COMMENT '是否同步用户' AFTER `work_group_flag`,\n" +
            "MODIFY COLUMN `message_flag` smallint(6) NULL DEFAULT 0 COMMENT '是否开启消息发送' AFTER `user_flag`;")
    }
}