package script.db.groovy.hzero_platform

databaseChangeLog(logicalFilePath: 'script/db/fd_user_guide.groovy') {
    changeSet(author: 'wanghao', id: '2021-05-18-fd_user_guide') {
        createTable(tableName: "FD_USER_GUIDE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT')
            }
            column(name: 'CODE', type: 'VARCHAR(256)', remarks: '指引编码') {
                constraints(nullable: false)
            }
            column(name: 'TITLE', type: 'VARCHAR(256)', remarks: '标题') {
                constraints(nullable: false)
            }
            column(name: 'MENU_ID', type: 'BIGINT', remarks: '菜单id') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_USER_GUIDE', columnNames: 'MENU_ID', constraintName: 'UK_FD_USER_GUIDE_U1')
        addUniqueConstraint(tableName: 'FD_USER_GUIDE', columnNames: 'CODE', constraintName: 'UK_FD_USER_GUIDE_U2')

    }
}