package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_db_ide_log.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_db_ide_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_db_ide_log_s', startValue:"1")
        }
        createTable(tableName: "hpfm_db_ide_log", remarks: "DB_IDE日志表") {
            column(name: "log_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint(20)",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "schema_name", type: "varchar(" + 60 * weight + ")",  remarks: "数据库名")  {constraints(nullable:"false")}  
            column(name: "table_name", type: "varchar(" + 60 * weight + ")",  remarks: "表名称")  {constraints(nullable:"false")}  
            column(name: "column_name", type: "varchar(" + 240 * weight + ")",  remarks: "字段名称，多个字段使用逗号分隔")   
            column(name: "operation", type: "varchar(" + 30 * weight + ")",  remarks: "操作类型，CREATE,DROP,ALTER…")  {constraints(nullable:"false")}  
            column(name: "oper_object", type: "varchar(" + 30 * weight + ")",  remarks: "操作对象，TABLE,COLUMN,INDEX")  {constraints(nullable:"false")}  
            column(name: "time", type: "int(20)",  remarks: "执行时间，毫秒")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_db_ide_log", indexName: "hpfm_db_ide_log_n1") {
            column(name: "tenant_id")
        }

    }
}