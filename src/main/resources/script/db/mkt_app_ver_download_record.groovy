package script.db

databaseChangeLog(logicalFilePath: 'script/db/mkt_app_ver_download_record.groovy') {
    changeSet(author: 'zongw.lee@gmail.com', id: '2019-08-21-mkt-app-ver-download-record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_APP_VER_DOWNLOAD_RECORD_S', startValue: "1")
        }
        createTable(tableName: "MKT_APP_VER_DOWNLOAD_RECORD", remarks: "应用版本下载历史记录表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MKT_APP_VER_DOWNLOAD_RECORD')
            }
            column(name: 'MKT_APP_ID', type: 'BIGINT UNSIGNED', remarks: 'SasS应用Id') {
                constraints(nullable: false)
            }
            column(name: 'MKT_APP_CODE', type: 'VARCHAR(64)', remarks: 'SasS应用Code') {
                constraints(nullable: false)
            }
            column(name: 'MKT_APP_NAME', type: 'VARCHAR(64)', remarks: 'SasS应用名称') {
                constraints(nullable: false)
            }
            column(name: 'MKT_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: 'SaaS应用版本Id') {
                constraints(nullable: false)
            }
            column(name: 'MKT_VERSION_NAME', type: 'VARCHAR(64)', remarks: 'SaaS应用版本名称') {
                constraints(nullable: false)
            }
            column(name: 'CATEGORY_NAME', type: 'VARCHAR(50)', remarks: 'SaaS应用类型名称') {
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
    }
    changeSet(author: 'wanghao', id: '2020-01-03-mkt-app-ver-download-record-drop') {
        dropTable(tableName: "MKT_APP_VER_DOWNLOAD_RECORD")
    }
}