package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard.groovy') {
    def weight_c = 1
    if(helper.isOracle()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard-2021-07-07-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'fd_dashboard_s', startValue:"1")
        }
        createTable(tableName: "fd_dashboard", remarks: "") {
            column(name: "dashboard_id", type: "bigint",autoIncrement: true,    remarks: "面板ID")  {constraints(primaryKey: true)} 
            column(name: "dashboard_type", type: "varchar(" + 30* weight_c + ")",   defaultValue:"CUSTOMIZE",   remarks: "面板类型(CUSTOMIZE/自定义;INTERNAL/内置)")  {constraints(nullable:"false")}  
            column(name: "dashboard_name", type: "varchar(" + 480* weight_c + ")",  remarks: "面板名称")  {constraints(nullable:"false")}  
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "默认面板")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
       createIndex(tableName: "fd_dashboard", indexName: "fd_dashboard_n1") {
           column(name: "dashboard_type")
           column(name: "dashboard_name")
       }
    }
}
