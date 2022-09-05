package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_report_project_category.groovy') {
    changeSet(author: 'scp', id: '2021-01-13-fd-report') {
        createTable(tableName: "FD_REPORT_PROJECT_CATEGORY", remarks: "报表与项目类型关系表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REPORT_CATEGORY')
            }
            column(name: 'REPORT_ID', type: 'VARCHAR(64)', remarks: '报表Id')
            column(name: 'PROJECT_CATEGORY', type: 'VARCHAR(64)', remarks: '项目类型')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    // 新增多语言，数据结构调整，需要重新初始化
    changeSet(author: 'wanghao', id: '2021-12-07-fd_report-drop-column') {
        sql("DELETE FROM FD_REPORT_PROJECT_CATEGORY")
    }
}