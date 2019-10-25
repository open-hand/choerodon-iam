package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_category_menu.groovy') {
    changeSet(author: 'jiameng.cao@hand-china.com', id: '2019-06-03-fd-category_menu.groovy') {
        createTable(tableName: "FD_CATEGORY_MENU") {
            column(name: 'ID', type: 'BIGINT(20)', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_CATEGORY_MENU')
            }
            column(name: 'CATEGORY_CODE', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'MENU_CODE', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'RESOURCE_LEVEL', type: 'VARCHAR(255)', remarks: 'category类型，organization 组织下的， organization_project 组织下的项目下的， project 项目下的') {
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_CATEGORY_MENU', columnNames: 'CATEGORY_CODE,MENU_CODE,RESOURCE_LEVEL', constraintName: 'UK_IAM_CATEGORY_MENU_U1')
    }

}