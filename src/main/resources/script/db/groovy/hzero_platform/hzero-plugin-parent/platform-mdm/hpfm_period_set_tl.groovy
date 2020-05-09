package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_period_set_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_period_set_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_period_set_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_period_set_tl", remarks: "会计期定义多语言") {
            column(name: "period_set_id", type: "bigint(20)",  remarks: "会计期ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "period_set_name", type: "varchar(" + 240 * weight + ")",  remarks: "会计期名称")   

        }

        addUniqueConstraint(columnNames:"period_set_id,lang",tableName:"hpfm_period_set_tl",constraintName: "hpfm_period_set_tl_u1")
    }
}