package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_customize_rule.groovy') {
    changeSet(author: "jiangzhou.bo@hand-china.com", id: "2019-07-25-hpfm_customize_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_customize_rule_s', startValue:"1")
        }
        createTable(tableName: "hpfm_customize_rule", remarks: "API个性化规则") {
            column(name: "rule_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "rule_code", type: "varchar(" + 30 * weight + ")",  remarks: "规则编码")  {constraints(nullable:"false")}  
            column(name: "rule_name", type: "varchar(" + 120 * weight + ")",  remarks: "规则名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "type_code", type: "varchar(" + 30 * weight + ")",  remarks: "规则类别编码，HPFM.CUSTOMIZE_RULE_TYPE")  {constraints(nullable:"false")}  
            column(name: "type_value", type: "longtext",  remarks: "规则类别对应的值")  {constraints(nullable:"false")}  
            column(name: "rule_position", type: "varchar(" + 30 * weight + ")",  remarks: "规则位置，HPFM.CUSTOMIZE_RULE_POSITION")  {constraints(nullable:"false")}  
            column(name: "sync_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "是否同步调用 1：同步；0异步；默认1；")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rule_code,tenant_id",tableName:"hpfm_customize_rule",constraintName: "hpfm_customize_rule_u1")
    }
}