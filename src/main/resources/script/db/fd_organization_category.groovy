package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_organization_category.groovy') {
    changeSet(author: 'jiameng.cao@hand-china.com', id: '2019-06-03-fd-organization-category') {
        createTable(tableName: "FD_ORGANIZATION_CATEGORY") {
            column(name: 'ID', type: 'BIGINT(20)', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_ORGANIZATION_CATEGORY')
            }
            column(name: 'CODE', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '')
            column(name: 'NAME', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'BUILT_IN_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否预定义', defaultValue: "1") {
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_ORGANIZATION_CATEGORY', columnNames: 'CODE', constraintName: 'UK_IAM_ORGANIZATION_CATEGORY_U1')
    }
}