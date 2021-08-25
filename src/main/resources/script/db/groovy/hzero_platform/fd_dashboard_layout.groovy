package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_layout.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_layout-2021-07-07-version-1"){
        createTable(tableName: "fd_dashboard_layout", remarks: "") {
            column(name: "layout_id", type: "BIGINT(20)",autoIncrement: true,    remarks: "卡片布局ID")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "BIGINT(20)",  remarks: "用户ID")   
            column(name: "dashboard_id", type: "BIGINT(20)",  remarks: "面板ID")  {constraints(nullable:"false")}  
            column(name: "card_id", type: "BIGINT(20)",  remarks: "卡片ID")  {constraints(nullable:"false")}  
            column(name: "w", type: "INT(4)",  remarks: "卡片宽")  {constraints(nullable:"false")}  
            column(name: "h", type: "INT(11)",  remarks: "卡片高")  {constraints(nullable:"false")}  
            column(name: "x", type: "INT(4)",  remarks: "卡片位置x")  {constraints(nullable:"false")}  
            column(name: "y", type: "INT(11)",  remarks: "卡片位置y")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "BIGINT(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"card_id,dashboard_id",tableName:"fd_dashboard_layout",constraintName: "fd_dashboard_layout_u1")
    }
}
