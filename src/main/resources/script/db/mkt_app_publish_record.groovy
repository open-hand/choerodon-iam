package script.db

/**
 * 市场应用发布记录表
 */
databaseChangeLog(logicalFilePath: 'script/db/mkt_app_publish_record.groovy') {
    changeSet(author: 'relaxingchu@qq.com', id: '2019-08-20-mkt_app_publish_record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_APP_PUBLISH_RECORD_S', startValue: "1")
        }
        createTable(tableName: "mkt_app_publish_record",remarks: "市场应用发布记录表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'MKT_APP_PUBLISH_RECORD')
            }

            column(name: "MKT_APP_CODE", type: 'VARCHAR(64)', remarks: '市场应用code') {
                constraints(nullable: false)
            }

            column(name: "MKT_APP_VERSION", type: 'VARCHAR(128)', remarks: '市场应用version引用') {
                constraints(nullable: false)
            }

            column(name: "PUBLISH_USER_ID", type: "BIGINT UNSIGNED") {
                constraints(nullable: false)
            }

            column(name: "PUBLISH_STATUS", type: 'VARCHAR(32)', defaultValue: "pending", remarks: '发布状态，默认为 待处理') {
                constraints(nullable: false)
            }

            column(name: 'PUBLISH_ERROR_CODE', type: 'VARCHAR(255)', remarks: '发布错误信息(仅在发布失败时有值)')

            column(name: "HANDLE_TIME", type: "DATETIME", remarks: '处理时间') {
                constraints(nullable: true)
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
    changeSet(author: 'wanghao', id: '2020-01-03-mkt-app-publish-record-drop') {
        dropTable(tableName: "mkt_app_publish_record")
    }
}