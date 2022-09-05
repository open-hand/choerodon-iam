package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_project_map_category.groovy') {
    changeSet(author: 'shiying.shen@hand-china.com', id: '2019-06-12-fd_project_map_category') {
        createTable(tableName: "FD_PROJECT_MAP_CATEGORY", remarks: "项目与项目类型关联表") {
            column(name: 'ID', type: 'BIGINT(20)', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT_MAP_CATEGORY')
            }
            column(name: 'PROJECT_ID', type: 'BIGINT(20)', remarks: '项目ID') {
                constraints(nullable: false)
            }
            column(name: 'CATEGORY_ID', type: 'BIGINT(20)', remarks: '项目类型ID') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        addUniqueConstraint(tableName: 'FD_PROJECT_MAP_CATEGORY', columnNames: 'PROJECT_ID,CATEGORY_ID', constraintName: 'UK_PROJECT_MAP_CATEGORY_U1')
    }
}