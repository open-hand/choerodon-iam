package script.db
/**
 * fd_remote_token.groovy
 * 远程连接TOKEN表 (id,organization_id,name,email,remote_token,is_expired)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_remote_token.groovy') {
    changeSet(id: '2019-07-30-fd_remote_token', author: 'longhe6699@gmail.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_REMOTE_TOKEN_S', startValue: "1")
        }
        createTable(tableName: "FD_REMOTE_TOKEN",remarks: "远程连接令牌表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: "PK_FD_REMOTE_TOKEN")
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织ID') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: 'TOKEN的名称') {
                constraints(nullable: false)
            }
            column(name: 'EMAIL', type: 'VARCHAR(128)', remarks: '联系邮箱') {
                constraints(nullable: false)
            }
            column(name: 'REMOTE_TOKEN', type: 'VARCHAR(128)', remarks: '远程连接的UUID') {
                constraints(unique: true, uniqueConstraintName: 'UK_FD_REMOTE_TOKEN_U1')
                constraints(nullable: false)
            }
            column(name: 'IS_EXPIRED', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: 'token是否过期。0：（默认）没有过期；1：已过期。') {
                constraints(nullable: false)
            }
            column(name: "LATEST_EXPIRATION_TIME", type: "DATETIME",remarks: '最近一次的失效时间')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(author: 'wanghao', id: '2020-01-03-fd-remote-token-drop') {
        dropTable(tableName: "FD_REMOTE_TOKEN")
    }
}
