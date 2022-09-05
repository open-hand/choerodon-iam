package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_user.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_user-2021-07-07-version-1"){
        createTable(tableName: "fd_dashboard_user", remarks: "面板用户关系表") {
            column(name: "dashboard_user_id", type: "BIGINT(20)",autoIncrement: true,    remarks: "面板用户ID")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "BIGINT(20)",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "dashboard_id", type: "BIGINT(20)",  remarks: "面板ID")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "BIGINT(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"dashboard_id,user_id",tableName:"fd_dashboard_user",constraintName: "fd_dashboard_user_u1")
    }

    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_user-2021-07-15-version-2") {
        addColumn (tableName: "fd_dashboard_user") {
            column (name: "rank", type: "INT(11)", remarks: "序号", afterColumn: "dashboard_id", defaultValue: "0") {
                constraints (nullable: "false")
            }
        }
    }
}
