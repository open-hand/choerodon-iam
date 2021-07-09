package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_user.groovy') {
    def weight_c = 1
    if(helper.isOracle()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_user-2021-07-07-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'fd_dashboard_user_s', startValue:"1")
        }
        createTable(tableName: "fd_dashboard_user", remarks: "") {
            column(name: "dashboard_user_id", type: "bigint",autoIncrement: true,    remarks: "面板用户ID")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "dashboard_id", type: "bigint",  remarks: "面板ID")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"dashboard_id,user_id",tableName:"fd_dashboard_user",constraintName: "fd_dashboard_user_u1")
    }
}
