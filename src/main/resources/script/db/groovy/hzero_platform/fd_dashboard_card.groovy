package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_card.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_card-2021-07-09-version-2"){
        createTable(tableName: "fd_dashboard_card", remarks: "") {
            column(name: "card_id", type: "BIGINT(20)",autoIncrement: true,    remarks: "卡片ID")  {constraints(primaryKey: true)} 
            column(name: "fd_level", type: "VARCHAR(30)",  remarks: "层级(SITE/平台层,ORGANIZATION/组织层,PROJECT/项目层)")  {constraints(nullable:"false")}  
            column(name: "group_id", type: "VARCHAR(30)",  remarks: "分类")   
            column(name: "card_code", type: "VARCHAR(150)",  remarks: "卡片编码")  {constraints(nullable:"false")}  
            column(name: "card_name", type: "VARCHAR(480)",  remarks: "卡片名称")  {constraints(nullable:"false")}  
            column(name: "link_service", type: "VARCHAR(60)",  remarks: "关联的服务编码")   
            column(name: "w", type: "INT(4)",  remarks: "卡片默认宽")  {constraints(nullable:"false")}  
            column(name: "h", type: "INT(11)",  remarks: "卡盘默认高")  {constraints(nullable:"false")}  
            column(name: "min_w", type: "INT(4)",  remarks: "卡片最小宽")   
            column(name: "min_h", type: "INT(11)",  remarks: "卡片最小高")   
            column(name: "max_w", type: "INT(4)",  remarks: "卡片最大宽")   
            column(name: "max_h", type: "INT(11)",  remarks: "卡片最大高")   
            column(name: "created_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "BIGINT(20)",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "DATETIME",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "BIGINT(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"card_code",tableName:"fd_dashboard_card",constraintName: "fd_dashboard_card_u1")
    }
}
