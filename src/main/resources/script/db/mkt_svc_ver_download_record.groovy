package script.db

databaseChangeLog(logicalFilePath: 'script/db/mkt_svc_ver_download_record.groovy.groovy') {
    changeSet(author: 'zongw.lee@gmail.com', id: '2019-09-06-mkt-svc-ver-download-record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_SVC_VER_DOWNLOAD_RECORD_S', startValue: "1")
        }
        createTable(tableName: "MKT_SVC_VER_DOWNLOAD_RECORD", remarks: "已下载的市场应用版本与服务版本的关联表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MKT_SVC_VER_DOWNLOAD_RECORD')
            }
            column(name: 'MKT_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '市场应用版本Id') {
                constraints(nullable: false)
            }
            column(name: 'MKT_SVC_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '市场服务版本Id') {
                constraints(nullable: false)
            }
            column(name: 'STATUS', type: 'VARCHAR(16)', remarks: '下载状态：下载中downloading，完成completed，失败failed') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织ID')

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
        createIndex(tableName: "MKT_SVC_VER_DOWNLOAD_RECORD", indexName: "IDX_MKT_VERSION_ID") {
            column(name: "MKT_VERSION_ID")
        }
    }
    changeSet(author: 'wanghao', id: '2020-01-03-mkt-svc-ver-download-record-drop') {
        dropTable(tableName: "MKT_SVC_VER_DOWNLOAD_RECORD")
    }
}