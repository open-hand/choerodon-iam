package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_ldap_history.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_ldap_history") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_ldap_history_s', startValue:"1")
        }
        createTable(tableName: "oauth_ldap_history", remarks: "") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "ldap_id", type: "bigint(20)",  remarks: "ldap id")  {constraints(nullable:"false")}  
            column(name: "new_user_count", type: "int(10)",  remarks: "同步用户新增数量")   
            column(name: "update_user_count", type: "int(10)",  remarks: "同步用户更新数量")   
            column(name: "error_user_count", type: "int(10)",  remarks: "同步用户失败数量")   
            column(name: "sync_begin_time", type: "datetime",  remarks: "同步开始时间")   
            column(name: "sync_end_time", type: "datetime",  remarks: "同步结束时间")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

    }
}