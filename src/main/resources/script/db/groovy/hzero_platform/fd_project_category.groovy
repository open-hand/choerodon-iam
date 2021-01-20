package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_project_category.groovy') {
    changeSet(author: 'jiameng.cao@hand-china.com', id: '2019-06-03-fd-project-category') {
        createTable(tableName: "FD_PROJECT_CATEGORY") {
            column(name: 'ID', type: 'BIGINT(20)', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT_CATEGORY')
            }
            column(name: 'CODE', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'LABEL_CODE', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '')
            column(name: 'NAME', type: 'VARCHAR(255)', remarks: '') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT(20)', remarks: '平台预定义的项目类型，就是0', defaultValue: "0") {
                constraints(nullable: false)
            }
            column(name: 'DISPLAY_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否允许被选到', defaultValue: "1")
            column(name: 'BUILT_IN_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否预定义', defaultValue: "0")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_PROJECT_CATEGORY', columnNames: 'CODE', constraintName: 'UK_IAM_PROJECT_CATEGORY_U1')

    }

    changeSet(author: 'scp', id: '2021-01-20-fd_project_category-add-column') {
        addColumn(tableName: 'fd_project_category') {
            column(name: 'sequence', type: "BIGINT UNSIGNED", remarks: '项目类型顺序', defaultValue: '60', afterColumn: 'ORGANIZATION_ID')
        }
    }
}