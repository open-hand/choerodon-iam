package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_region_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hpfm_exchange_rate_type_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_exchange_rate_type_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_exchange_rate_type_tl", remarks: "汇率类型定义多语言") {
            column(name: "rate_type_id", type: "bigint(20)",  remarks: "hpfm_exchange_rate_type.rate_type_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "type_name", type: "varchar(" + 30 * weight + ")",  remarks: "汇率类型名称")  {constraints(nullable:"false")}

        }
        addUniqueConstraint(columnNames:"rate_type_id,lang",tableName:"hpfm_exchange_rate_type_tl",constraintName: "hpfm_exchange_rate_type_tl_u1")
    }
}
