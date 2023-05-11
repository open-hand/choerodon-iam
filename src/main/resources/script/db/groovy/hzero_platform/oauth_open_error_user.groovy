package script.db.groovy

databaseChangeLog(logicalFilePath: 'script/db/oauth_open_app_error_user.groovy') {
    changeSet(author: 'changping.shi@hand-china.com', id: '2022-03-23-oauth_open_app_error_user') {
        createTable(tableName: "oauth_open_app_error_user", remarks: "第三方应用同步记录表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键， bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LDAP_ERROR_USER')
            }
            column(name: 'OPEN_HISTORY_ID', type: 'BIGINT UNSIGNED', remarks: 'openApp同步历史id') {
                constraints(nullable: false)
            }
            column(name: "TENANT_ID", type: "BIGINT UNSIGNED", defaultValue: "0", remarks: "租户id") {
                constraints(nullable: "false")
            }
            column(name: 'LOGIN_NAME', type: "VARCHAR(128)", remarks: '用户登录名')
            column(name: 'EMAIL', type: "VARCHAR(128)", remarks: '用户邮箱')
            column(name: 'REAL_NAME', type: "VARCHAR( 128 )", remarks: '真实姓名')
            column(name: "PHONE", type: "VARCHAR(128)", remarks: '手机号')
            column(name: "CAUSE", type: "VARCHAR(128)", remarks: '失败原因')

            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "更新时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
        }
        createIndex(indexName: "idx_open_history_id", tableName: "oauth_open_app_error_user") {
            column(name: "open_history_id")
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-04-28_add_column') {
        addColumn(tableName: 'oauth_open_app_error_user') {
            column(name: "department", type: "VARCHAR(128)", remarks: "部门", afterColumn: "EMAIL")
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-05-06_add_column') {
        addColumn(tableName: 'oauth_open_app_error_user') {
            column(name: "type", type: "VARCHAR(128)", remarks: "类型", afterColumn: "department")
        }
    }

}