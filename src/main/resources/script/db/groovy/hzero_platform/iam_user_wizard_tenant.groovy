package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_user_wizard_tenant.groovy') {
    changeSet(author: 'scp', id: '2021-09-27-iam-user-wizard-tenant') {
        createTable(tableName: "IAM_USER_WIZARD_TENANT") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REPORT')
            }
            column(name: 'WIZARD_ID', type: 'BIGINT UNSIGNED', remarks: '向导Id')
            column(name: 'TENANT_ID', type: 'BIGINT UNSIGNED', remarks: '组织Id')
            column(name: 'STATUS', type: 'VARCHAR(30)', remarks: '排序',defaultValue: "uncompleted")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'IAM_USER_WIZARD_TENANT', columnNames: 'WIZARD_ID,TENANT_ID', constraintName: 'UK_IAM_USER_WIZARD_TENANT_U1')
    }
}