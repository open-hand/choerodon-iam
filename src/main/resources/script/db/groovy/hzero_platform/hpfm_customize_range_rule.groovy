package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_customize_range_rule.groovy') {
    changeSet(author: "jiangzhou.bo@hand-china.com", id: "2019-07-25-hpfm_customize_range_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_customize_range_rule_s', startValue:"1")
        }
        createTable(tableName: "hpfm_customize_range_rule", remarks: "API个性化范围规则关系") {
            column(name: "range_rule_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "range_id", type: "bigint(20)",  remarks: "范围ID，hpfm_customize_range.range_id")  {constraints(nullable:"false")}  
            column(name: "rule_id", type: "bigint(20)",  remarks: "规则ID，hpfm_customize_rule.rule_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"range_id,rule_id",tableName:"hpfm_customize_range_rule",constraintName: "hpfm_customize_range_rule_u1")
    }
}