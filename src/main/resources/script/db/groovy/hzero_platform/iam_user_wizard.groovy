package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_user_wizard.groovy') {
    changeSet(author: 'scp', id: '2021-09-27-iam-user-wizard') {
        createTable(tableName: "IAM_USER_WIZARD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REPORT')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '名称') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_IAM_USER_WIZARD_NAME_U1')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '编码') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_IAM_USER_WIZARD_CODE_U1')
            }
            column(name: 'SORT', type: 'TINYINT UNSIGNED', remarks: '排序')
            column(name: 'ICON', type: 'MEDIUMTEXT', remarks: '图标')
            column(name: 'DESCRIPTION', type: 'TEXT', remarks: '描述')
            column(name: 'OPERATION_LINK', type: 'VARCHAR(128)', remarks: '链接')
            column(name: 'REMARK', type: 'TEXT', remarks: '提示')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}