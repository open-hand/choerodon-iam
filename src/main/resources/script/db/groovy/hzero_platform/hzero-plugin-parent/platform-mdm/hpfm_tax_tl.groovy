package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tax_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_tax_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_tax_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_tax_tl", remarks: "税率定义多语言") {
            column(name: "tax_id", type: "bigint(20)",  remarks: "税率ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"tax_id,lang",tableName:"hpfm_tax_tl",constraintName: "hpfm_tax_tl_u1")
    }
}