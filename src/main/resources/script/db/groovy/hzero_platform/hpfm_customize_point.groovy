package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_customize_point.groovy') {
    changeSet(author: "jiangzhou.bo@hand-china.com", id: "2019-07-25-hpfm_customize_point") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_customize_point_s', startValue:"1")
        }
        createTable(tableName: "hpfm_customize_point", remarks: "API个性化切入点") {
            column(name: "point_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名")  {constraints(nullable:"false")}  
            column(name: "package_name", type: "varchar(" + 240 * weight + ")",  remarks: "包名")  {constraints(nullable:"false")}  
            column(name: "class_name", type: "varchar(" + 180 * weight + ")",  remarks: "类名")  {constraints(nullable:"false")}  
            column(name: "method_name", type: "varchar(" + 180 * weight + ")",  remarks: "方法名")  {constraints(nullable:"false")}  
            column(name: "method_args", type: "longtext",  remarks: "方法参数列表")   
            column(name: "method_description", type: "varchar(" + 480 * weight + ")",  remarks: "方法描述")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_customize_point", indexName: "hpfm_customize_point_n1") {
            column(name: "service_name")
            column(name: "package_name")
            column(name: "class_name")
            column(name: "method_name")
        }

    }
}