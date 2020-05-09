package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_currency_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_currency_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_currency_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_currency_tl", remarks: "币种信息多语言") {
            column(name: "currency_id", type: "bigint(20)",  remarks: "币种ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "currency_name", type: "varchar(" + 120 * weight + ")",  remarks: "币种名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"currency_id,lang",tableName:"hpfm_currency_tl",constraintName: "hpfm_currency_tl_u1")
    }
}