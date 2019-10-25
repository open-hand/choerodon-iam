package script.db
/**
 * fd_remote_connection_record.groovy
 * 远程连接测试记录表 (id,remote_token_id,source_ip,status)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_remote_connection_record.groovy') {
    changeSet(id: '2019-07-30-fd_remote_connection_record', author: 'longhe6699@gmail.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_REMOTE_CONNECTION_RECORD_S', startValue: "1")
        }
        createTable(tableName: "FD_REMOTE_CONNECTION_RECORD",remarks: "远程连接记录表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: "PK_FD_REMOTE_CONNECTION_RECORD")
            }
            column(name: 'REMOTE_TOKEN_ID', type: 'BIGINT UNSIGNED', remarks: '远程连接Token主键') {
                constraints(nullable: false)
            }
            column(name: 'SOURCE_IP', type: 'VARCHAR(64)', remarks: '来源IP') {
                constraints(nullable: false)
            }
            column(name: 'OPERATION', type: 'VARCHAR(32)', defaultValue: "configure_and_test", remarks: '操作类型。配置并测试、断开连接、重新连接。') {
                constraints(nullable: false)
            }


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}
