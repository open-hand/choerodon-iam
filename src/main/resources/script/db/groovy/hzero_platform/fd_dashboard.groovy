package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard-2021-07-07-version-1"){
        createTable(tableName: "fd_dashboard", remarks: "") {
            column(name: "dashboard_id", type: "BIGINT(20)",autoIncrement: true,    remarks: "面板ID")  {constraints(primaryKey: true)} 
            column(name: "dashboard_type", type: "VARCHAR(30)",   defaultValue:"CUSTOMIZE",   remarks: "面板类型(CUSTOMIZE/自定义;INTERNAL/内置)")  {constraints(nullable:"false")}  
            column(name: "dashboard_name", type: "VARCHAR(480)",  remarks: "面板名称")  {constraints(nullable:"false")}  
            column(name: "default_flag", type: "TINYINT(1)",   defaultValue:"0",   remarks: "默认面板")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "DATETIME",   defaultValue:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "DATETIME",   defaultValue:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "BIGINT(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
       createIndex(tableName: "fd_dashboard", indexName: "fd_dashboard_n1") {
           column(name: "dashboard_type")
           column(name: "dashboard_name")
       }
    }
}
