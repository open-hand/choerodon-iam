package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_data_hierarchy_tl.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-09-04-hpfm_data_hierarchy_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_data_hierarchy_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_data_hierarchy_tl", remarks: "数据层级配置多语言") {
            column(name: "data_hierarchy_id", type: "bigint(20)",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "data_hierarchy_name", type: "varchar(" + 60 * weight + ")",   defaultValue:" ",   remarks: "数据层级名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"data_hierarchy_id,lang",tableName:"hpfm_data_hierarchy_tl",constraintName: "hpfm_data_hierarchy_tl_u1")
    }
}