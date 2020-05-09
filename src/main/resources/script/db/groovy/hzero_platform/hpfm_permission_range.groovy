package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_permission_range.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_permission_range") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_permission_range_s', startValue:"1")
        }
        createTable(tableName: "hpfm_permission_range", remarks: "屏蔽范围") {
            column(name: "range_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "custom_rule_flag", type: "tinyint(1)",   defaultValue:"0",   remarks: "自定义规则标识")  {constraints(nullable:"false")}  
            column(name: "table_name", type: "varchar(" + 60 * weight + ")",  remarks: "屏蔽表名")  {constraints(nullable:"false")}  
            column(name: "sql_id", type: "varchar(" + 120 * weight + ")",  remarks: "屏蔽sqlId")   
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "屏蔽描述")   
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名称")   
            column(name: "db_prefix", type: "varchar(" + 60 * weight + ")",  remarks: "数据库前缀")   
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"table_name,service_name,tenant_id,sql_id",tableName:"hpfm_permission_range",constraintName: "hpfm_permission_range_u1")
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-03-27-hpfm_permission_range") {
        dropColumn(tableName: "hpfm_permission_range", columnName: "db_prefix")
    }
}