package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_database_tenant.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_database_tenant") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_database_tenant_s', startValue:"1")
        }
        createTable(tableName: "hpfm_database_tenant", remarks: "租户数据库关系") {
            column(name: "database_tenant_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "database_id", type: "bigint(20)",  remarks: "数据库ID")  {constraints(nullable:"false")}  
            column(name: "datasource_id", type: "bigint(20)",  remarks: "数据源ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"datasource_id,tenant_id",tableName:"hpfm_database_tenant",constraintName: "hpfm_database_tenant_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-06-20-hpfm_database_tenant") {
        dropUniqueConstraint(tableName: "hpfm_database_tenant",constraintName: "hpfm_database_tenant_u1")
        addUniqueConstraint(columnNames: "database_id, tenant_id", tableName: "hpfm_database_tenant", constraintName: "hpfm_database_tenant_u1")
    }
}
