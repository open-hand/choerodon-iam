package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_permission_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_permission_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_permission_rule_s', startValue:"1")
        }
        createTable(tableName: "hpfm_permission_rule", remarks: "屏蔽规则") {
            column(name: "rule_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "rule_code", type: "varchar(" + 30 * weight + ")",  remarks: "屏蔽规则code")  {constraints(nullable:"false")}  
            column(name: "rule_type_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"SQL",   remarks: "权限规则类型,值集:HPFM.PERMISSION_RULE_TYPE")  {constraints(nullable:"false")}  
            column(name: "rule_name", type: "varchar(" + 120 * weight + ")",  remarks: "屏蔽规则名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   
            column(name: "sql_value", type: "longtext",  remarks: "屏蔽sql值")   
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rule_code,tenant_id",tableName:"hpfm_permission_rule",constraintName: "hpfm_permission_rule_u1")
    }
}