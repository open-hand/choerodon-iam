package script.db
databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_card.groovy') {
    def weight_c = 1
    if(helper.isOracle()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "jian.zhang02@hand-china.com", id: "fd_dashboard_card-2021-07-09-version-2"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'fd_dashboard_card_s', startValue:"1")
        }
        createTable(tableName: "fd_dashboard_card", remarks: "") {
            column(name: "card_id", type: "bigint",autoIncrement: true,    remarks: "卡片ID")  {constraints(primaryKey: true)} 
            column(name: "fd_level", type: "varchar(" + 30* weight_c + ")",  remarks: "层级(SITE/平台层,ORGANIZATION/组织层,PROJECT/项目层)")  {constraints(nullable:"false")}  
            column(name: "group_id", type: "varchar(" + 30* weight_c + ")",  remarks: "分类")   
            column(name: "card_code", type: "varchar(" + 150* weight_c + ")",  remarks: "卡片编码")  {constraints(nullable:"false")}  
            column(name: "card_name", type: "varchar(" + 480* weight_c + ")",  remarks: "卡片名称")  {constraints(nullable:"false")}  
            column(name: "link_service", type: "varchar(" + 60* weight_c + ")",  remarks: "关联的服务编码")   
            column(name: "w", type: "int",  remarks: "卡片默认宽")  {constraints(nullable:"false")}  
            column(name: "h", type: "int",  remarks: "卡盘默认高")  {constraints(nullable:"false")}  
            column(name: "min_w", type: "int",  remarks: "卡片最小宽")   
            column(name: "min_h", type: "int",  remarks: "卡片最小高")   
            column(name: "max_w", type: "int",  remarks: "卡片最大宽")   
            column(name: "max_h", type: "int",  remarks: "卡片最大高")   
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"card_code",tableName:"fd_dashboard_card",constraintName: "fd_dashboard_card_u1")
    }
}
