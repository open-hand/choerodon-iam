package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_uom_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_uom_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_uom_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_uom_tl", remarks: "计量单位定义多语言") {
            column(name: "uom_id", type: "bigint(20)",  remarks: "计量单位表id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "uom_name", type: "varchar(" + 60 * weight + ")",  remarks: "计量单位名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"uom_id,lang",tableName:"hpfm_uom_tl",constraintName: "hpfm_uom_tl_u1")
    }
}