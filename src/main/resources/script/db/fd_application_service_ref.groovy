package script.db
/**
 * fd_application_service_ref.groovy
 * 应用与服务关系表 (id,application_id,service_id)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_application_service_ref.groovy') {
    changeSet(id: '2019-09-10-fd_application_service_ref', author: 'longhe6699@gmail.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_APPLICATION_SERVICE_REF_S', startValue: "1")
        }
        createTable(tableName: "FD_APPLICATION_SERVICE_REF", remarks: "应用与服务关系表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: "PK_FD_APPLICATION_SERVICE_REF")
            }
            column(name: 'APPLICATION_ID', type: 'BIGINT UNSIGNED', remarks: '应用主键（base_service.fd_application.id）') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE_ID', type: 'BIGINT UNSIGNED', remarks: '服务主键（devops_service.devops_app_service.id）') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_APPLICATION_SERVICE_REF', columnNames: 'APPLICATION_ID,SERVICE_ID', constraintName: 'UK_FD_APPLICATION_SERVICE_REF_U1')
    }
/*    changeSet(author: 'wanghao', id: '2020-01-03-fd-application-service-ref-drop') {
        dropTable(tableName: "FD_APPLICATION_SERVICE_REF")
    }*/
}
