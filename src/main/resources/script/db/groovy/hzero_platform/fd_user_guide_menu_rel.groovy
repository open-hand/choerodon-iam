package script.db.groovy.hzero_platform

databaseChangeLog(logicalFilePath: 'script/db/fd_user_guide_menu_rel.groovy') {
    changeSet(author: 'wanghao', id: '2021-05-18-fd_user_guide_menu_rel') {
        createTable(tableName: "FD_USER_GUIDE_MENU_REL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT')
            }
            column(name: 'MENU_ID', type: 'BIGINT UNSIGNED', remarks: '菜单id') {
                constraints(nullable: false)
            }
            column(name: 'STEP_ID', type: 'BIGINT UNSIGNED', remarks: '步骤id') {
                constraints(nullable: false)
            }
            column(name: 'STEP_ORDER', type: 'BIGINT UNSIGNED', remarks: '步骤顺序') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_USER_GUIDE_MENU_REL', columnNames: 'MENU_ID,STEP_ID', constraintName: 'UK_FD_USER_GUIDE_MENU_REL_U1')

    }
}