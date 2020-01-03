package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_remote_token_authorization.groovy') {
    changeSet(id: '2019-08-05-fd_remote_token_authorization', author: 'zongw.lee@gmail.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_REMOTE_TOKEN_AUTHORIZATION_S', startValue: "1")
        }
        createTable(tableName: "FD_REMOTE_TOKEN_AUTHORIZATION", remarks: "Pass平台远程连接授权记录表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: "PK_FD_REMOTE_TOKEN_AUTHORIZATION")
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: 'TOKEN名称') {
                constraints(nullable: false)
            }
            column(name: 'EMAIL', type: 'VARCHAR(128)', remarks: '联系邮箱') {
                constraints(nullable: false)
            }
            column(name: 'REMOTE_TOKEN', type: 'VARCHAR(128)', remarks: '远程连接的UUID') {
                constraints(nullable: false)
            }
            column(name: 'AUTHORIZATION_URL', type: 'VARCHAR(255)', remarks: 'TOKEN校验地址') {
                constraints(nullable: false)
            }
            column(name: 'STATUS', type: 'VARCHAR(16)', remarks: 'token状态。 success：成功；failed：失败；break: 中断') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_NAME', type: 'VARCHAR(32)', remarks: 'SaaS组织名') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_CODE', type: 'VARCHAR(15)', remarks: 'SaaS组织code') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(author: 'wanghao', id: '2020-01-03-fd-remote-token-authorization-drop') {
        dropTable(tableName: "FD_REMOTE_TOKEN_AUTHORIZATION")
    }
}
