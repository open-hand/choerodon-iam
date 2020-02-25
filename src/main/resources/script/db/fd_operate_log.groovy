package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_operate_log.groovy') {
    changeSet(author: 'xiang.wang04@hand-china.com', id: '2020-02-24-fd-operate-log') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_OPERATE_LOG_S', startValue: "1")
        }
        createTable(tableName: "FD_OPERATE_LOG") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_OPERATE_LOG')
            }
            column(name: 'OPERATOR_ID', type: 'VARCHAR(32)', remarks: '操作者的id') {
                constraints(nullable: false)
            }
            column(name: 'TYPE', type: 'VARCHAR(32)', remarks: '操作的类型') {
                constraints(nullable: false)
            }
            column(name: 'LEVEL', type: 'VARCHAR(15)', remarks: '层级') {
                constraints(nullable: false)
            }
            column(name: 'METHOD', type: 'VARCHAR(128)', remarks: '执行方法') {
                constraints(nullable: false)
            }
            column(name: 'CONTENT', type: 'VARCHAR(128)', remarks: '操作内容') {
                constraints(nullable: false)
            }
            column(name: 'OPERATION_TIME', type: 'DATETIME', remarks: '操作时间') {
                constraints(nullable: false)
            }
            column(name: 'IS_SUCCESS', type: 'TINYINT UNSIGNED', remarks: '执行成功与否,1成功，0未成功') {
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
        createIndex(tableName: "FD_OPERATE_LOG", indexName: "IDX_LEVEL") {
            column(name: "LEVEL")
        }
    }

}