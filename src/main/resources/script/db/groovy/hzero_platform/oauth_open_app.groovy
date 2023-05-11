package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_open_app.groovy') {
    changeSet(author: "changping.shi@hand-china.com", id: "2021-02-25_oauth_open_app") {
        createTable(tableName: "oauth_open_app", remarks: "第三方应用表") {
            column(name: "id", type: "BIGINT UNSIGNED", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "BIGINT UNSIGNED", defaultValue: "0", remarks: "租户id") {
                constraints(nullable: "false")
            }
            column(name: "type", type: "varchar(20)", remarks: "三方应用类型：微信、企业微信、钉钉、其他OAuth2.0等") {
                constraints(nullable: "false")
            }
            column(name: "app_id", type: "varchar(100)", remarks: "三方平台上获取的appid") {
                constraints(nullable: "false")
            }
            column(name: "app_secret", type: "varchar(255)", remarks: "三方平台上获取的app secret") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint(1)", defaultValue: "1", remarks: "是否启用。1启用，0未启用") {
                constraints(nullable: "false")
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "更新时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
        }
        addUniqueConstraint(columnNames: "tenant_id,type", tableName: "oauth_open_app", constraintName: "oauth_open_app_U1")

    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-05-12_add_column') {
        addColumn(tableName: 'oauth_open_app') {
            column(name: "agent_id", type: 'varchar(50)', remarks: 'agentId')
        }
    }

    changeSet(author: 'lihao', id: '2022-05-23_add_column') {
        addColumn(tableName: 'oauth_open_app') {
            column(name: "description", type: 'varchar(1024)', remarks: '环境简述')
            column(name: "yqcloud_site", type: 'varchar(1024)', remarks: '燕千云地址')
        }
    }
}