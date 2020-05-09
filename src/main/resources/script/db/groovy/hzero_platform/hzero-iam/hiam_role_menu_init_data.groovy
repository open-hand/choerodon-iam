package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_role_menu_init_data.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_role_menu_init_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_role_menu_init_data_s', startValue:"1")
        }
        createTable(tableName: "hiam_role_menu_init_data", remarks: "角色菜单初始化数据表") {
            column(name: "init_data_id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "role_id", type: "bigint(20)",  remarks: "角色ID")   
            column(name: "menu_id", type: "bigint(20)",  remarks: "菜单ID")   
            column(name: "role_code", type: "varchar(" + 128 * weight + ")",  remarks: "角色代码")   
            column(name: "menu_code", type: "varchar(" + 128 * weight + ")",  remarks: "菜单代码")   
            column(name: "process_batch_id", type: "varchar(" + 50 * weight + ")",   defaultValue:"UNBATCHED",   remarks: "处理批号，可使用UUID")  {constraints(nullable:"false")}  
            column(name: "process_user_id", type: "bigint(20)",  remarks: "处理人")   
            column(name: "process_status", type: "varchar(" + 30 * weight + ")",   defaultValue:"PENDING",   remarks: "处理状态，代码HIAM.PROCESS_STATUS，PENDING/RUNNING/COMPLETE/ERROR")  {constraints(nullable:"false")}  
            column(name: "process_date", type: "datetime",  remarks: "处理时间")   
            column(name: "process_remark", type: "longtext",  remarks: "处理消息")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hiam_role_menu_init_data", indexName: "hiam_role_menu_init_data_n1") {
            column(name: "role_id")
            column(name: "menu_id")
            column(name: "process_status")
        }
   createIndex(tableName: "hiam_role_menu_init_data", indexName: "hiam_role_menu_init_data_n2") {
            column(name: "process_batch_id")
        }

    }
}