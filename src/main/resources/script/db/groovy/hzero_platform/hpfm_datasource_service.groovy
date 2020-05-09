package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_service.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_datasource_service") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_datasource_service_s', startValue:"1")
        }
        createTable(tableName: "hpfm_datasource_service", remarks: "") {
            column(name: "datasource_service_id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 128 * weight + ")",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "datasource_id", type: "bigint(20)",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}