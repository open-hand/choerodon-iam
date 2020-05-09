package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov", remarks: "LOV表") {
            column(name: "lov_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "lov_code", type: "varchar(" + 60 * weight + ")",  remarks: "LOV代码")  {constraints(nullable:"false")}  
            column(name: "lov_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "LOV数据类型: URL/SQL/FIXED")  {constraints(nullable:"false")}  
            column(name: "route_name", type: "varchar(" + 120 * weight + ")",  remarks: "目标路由")   
            column(name: "lov_name", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "值集名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "parent_lov_code", type: "varchar(" + 30 * weight + ")",  remarks: "父级LOV CODE")   
            column(name: "parent_tenant_id", type: "bigint(20)",  remarks: "父级值集租户ID")   
            column(name: "custom_sql", type: "longtext",  remarks: "自定义sql")   
            column(name: "custom_url", type: "varchar(" + 600 * weight + ")",  remarks: "查询URL")
            column(name: "value_field", type: "varchar(" + 30 * weight + ")",  remarks: "值字段")   
            column(name: "value_table_alias", type: "varchar(" + 30 * weight + ")",  remarks: "值字段所在表别名")   
            column(name: "display_field", type: "varchar(" + 30 * weight + ")",  remarks: "显示字段")   
            column(name: "meaning_table_alias", type: "varchar(" + 30 * weight + ")",  remarks: "含义字段所在表别名")   
            column(name: "must_page_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "是否必须分页")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"0",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_lov", indexName: "hpfm_lov_n1") {
            column(name: "parent_lov_code")
        }

        addUniqueConstraint(columnNames:"lov_code,tenant_id",tableName:"hpfm_lov",constraintName: "hpfm_lov_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-09-06-hpfm_lov") {
        addColumn(tableName: 'hpfm_lov') {
            column(name: "translation_sql", type: "longtext", remarks: "翻译sql")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com",id: "2019-09-07-hpfm_lov"){
        dropColumn(tableName: "hpfm_lov",columnName:"value_table_alias" )
        dropColumn(tableName: "hpfm_lov",columnName:"meaning_table_alias" )
    }
}