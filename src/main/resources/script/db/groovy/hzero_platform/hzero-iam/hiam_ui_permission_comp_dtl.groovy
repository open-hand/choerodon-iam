package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_ui_permission_comp_dtl.groovy') {
    changeSet(author: "jianbo.li@hand-chian.com", id: "2020-01-02-hiam_ui_permission_comp_dtl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_ui_permission_comp_dtl_s', startValue:"1")
        }
        createTable(tableName: "hiam_ui_permission_comp_dtl", remarks: "UI权限组件数据明细") {
            column(name: "ui_permission_comp_dtl_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "ui_permission_comp_id", type: "bigint(20)",  remarks: "权限组件数据ID")  {constraints(nullable:"false")}  
            column(name: "path", type: "varchar(" + 128 * weight + ")",  remarks: "API路径")  {constraints(nullable:"false")}  
            column(name: "method", type: "varchar(" + 64 * weight + ")",  remarks: "HTTP方法")  {constraints(nullable:"false")}  
            column(name: "fd_level", type: "varchar(" + 64 * weight + ")",  remarks: "API层级")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 128 * weight + ")",  remarks: "API所属服务")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hiam_ui_permission_comp_dtl", indexName: "hiam_ui_permission_comp_dtl_n1") {
            column(name: "ui_permission_comp_id")
        }

    }
}