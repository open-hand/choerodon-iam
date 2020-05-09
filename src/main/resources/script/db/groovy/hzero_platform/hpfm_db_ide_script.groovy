package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_db_ide_script.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_db_ide_script") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_db_ide_script_s', startValue:"1")
        }
        createTable(tableName: "hpfm_db_ide_script", remarks: "DB_IDE执行脚本记录") {
            column(name: "script_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "log_id", type: "bigint(20)",  remarks: "日志ID，hpfm_db_ide_log.log_id")  {constraints(nullable:"false")}  
            column(name: "original_sql", type: "longtext",  remarks: "原始SQL语句")  {constraints(nullable:"false")}  
            column(name: "oper_sql", type: "longtext",  remarks: "实际执行SQL，多条SQL使用分号分隔")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"log_id",tableName:"hpfm_db_ide_script",constraintName: "hpfm_db_ide_script_u1")
    }
}