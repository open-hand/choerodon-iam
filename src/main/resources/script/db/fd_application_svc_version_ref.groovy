package script.db
/**
 * fd_application_svc_version_ref.groovy
 * 应用版本与服务版本关系表 (id,application_version_id,service_version_id,status)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_application_svc_version_ref.groovy') {
    changeSet(author: 'longhe6699@gmail.com', id: '2019-09-10-fd_application_svc_version_ref') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_APPLICATION_SVC_VERSION_REF_S', startValue: "1")
        }
        createTable(tableName: "FD_APPLICATION_SVC_VERSION_REF", remarks: "应用版本与服务版本关系表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_APPLICATION_SVC_VERSION_REF')
            }
            column(name: 'APPLICATION_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '应用版本主键') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '服务版本主键') {
                constraints(nullable: false)
            }
            column(name: 'STATUS', type: 'VARCHAR(64)', defaultValue: 'unpublished', remarks: '发布状态。' +
                    'unpublished(默认):未发布;' +
                    'processing:发布中;' +
                    'failure:失败;' +
                    'done:发布完成。') {
                constraints(nullable: false)
            }


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        addUniqueConstraint(tableName: 'FD_APPLICATION_SVC_VERSION_REF', columnNames: 'APPLICATION_VERSION_ID, SERVICE_VERSION_ID', constraintName: 'UK_FD_APPLICATION_SVC_VERSION_REF_U1')
    }
/*    changeSet(author: 'wanghao', id: '2020-01-03-fd-application-svc-version-ref-drop') {
        dropTable(tableName: "FD_APPLICATION_SVC_VERSION_REF")
    }*/
}