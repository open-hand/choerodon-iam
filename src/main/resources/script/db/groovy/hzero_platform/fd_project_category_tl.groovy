package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_project_category_tl.groovy') {
    changeSet(author: 'changping.shi@zknow.com', id: '2022-07-14-fd_project_category_tl') {
        createTable(tableName: "FD_PROJECT_CATEGORY_TL", remarks: '项目类型多语言') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '项目类型主键') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(255)', remarks: '项目类型名称') {
            }

            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言') {
                constraints(nullable: false)
            }
        }
        addUniqueConstraint(tableName: 'FD_PROJECT_CATEGORY_TL', constraintName: 'fd_project_category_tl_u1', columnNames: 'ID,LANG')
    }
}