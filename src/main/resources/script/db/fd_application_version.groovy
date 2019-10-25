package script.db
/**
 * fd_application_version.groovy
 * 应用版本表 (id,application_id,version,description)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_application_version.groovy') {
    changeSet(author: 'longhe6699@gmail.com', id: '2019-09-10-fd_application_version') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_APPLICATION_VERSION_S', startValue: "1")
        }
        createTable(tableName: "FD_APPLICATION_VERSION", remarks: "应用版本表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_APPLICATION_VERSION')
            }
            column(name: 'APPLICATION_ID', type: 'BIGINT UNSIGNED', remarks: '应用ID') {
                constraints(nullable: false)
            }
            column(name: 'VERSION', type: 'VARCHAR(64)', remarks: '版本名称') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(5000)', remarks: '版本说明')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_APPLICATION_VERSION', columnNames: 'APPLICATION_ID, VERSION', constraintName: 'UK_FD_APPLICATION_VERSION_U1')
    }
}