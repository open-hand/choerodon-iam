package script.db.groovy.hzero_platform

databaseChangeLog(logicalFilePath: 'script/db/fd_help_doc.groovy') {
    changeSet(author: 'scp', id: '2021-06-09-fd_help_doc') {
        createTable(tableName: "FD_HELP_DOC") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'FD_HELP_DOC')
            }
            column(name: 'MENU_ID', type: 'BIGINT UNSIGNED', remarks: '菜单id')
            column(name: 'MENU_CODE', type: 'VARCHAR(64)', remarks: '菜单code,针对于没有菜单Id的url页面')
            column(name: 'TAB_CODE', type: 'VARCHAR(128)', remarks: 'tab页code，针对于不同tab页')
            column(name: 'PATH', type: 'VARCHAR(64)', remarks: '开放平台跳转地址'){
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_USER_GUIDE_MENU_REL', columnNames: 'MENU_ID,MENU_CODE,TAB_CODE', constraintName: 'UK_FD_HELP_DOC_U1')
    }
}