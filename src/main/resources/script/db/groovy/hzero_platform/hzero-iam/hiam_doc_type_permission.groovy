package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hiam_doc_type_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_permission_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type_permission", remarks: "单据类型数据权限关联") {
            column(name: "doc_type_permission_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "auth_dim_id", type: "bigint(20)",  remarks: "单据类型维度分配ID，hiam_doc_type_auth_dim.auth_dim_id")  {constraints(nullable:"false")}  
            column(name: "rule_id", type: "bigint(20)",  remarks: "数据权限范围ID,hpfm_permission_rule.rule_id")  {constraints(nullable:"false")}  
            column(name: "range_id", type: "bigint(20)",  remarks: "数据权限规则ID.hpfm_permission_range.range_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"auth_dim_id,rule_id,range_id",tableName:"hiam_doc_type_permission",constraintName: "hiam_doc_type_permission_u1")
    }
} 