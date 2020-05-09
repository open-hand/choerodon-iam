package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_database.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_database") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_database_s', startValue:"1")
        }
        createTable(tableName: "hpfm_database", remarks: "数据库") {
            column(name: "database_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "database_code", type: "varchar(" + 150 * weight + ")",  remarks: "数据库代码")  {constraints(nullable:"false")}  
            column(name: "database_name", type: "varchar(" + 150 * weight + ")",  remarks: "数据库名称")  {constraints(nullable:"false")}  
            column(name: "datasource_id", type: "bigint(20)",  remarks: "数据源ID")  {constraints(nullable:"false")}  
            column(name: "table_prefix", type: "varchar(" + 240 * weight + ")",  remarks: "表前缀")  {constraints(nullable:"false")}  
            column(name: "public_flag", type: "tinyint(1)",   defaultValue:"0",   remarks: "公共库标识")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "启用标识")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"database_code,datasource_id",tableName:"hpfm_database",constraintName: "hpfm_database_u1")
    }
}