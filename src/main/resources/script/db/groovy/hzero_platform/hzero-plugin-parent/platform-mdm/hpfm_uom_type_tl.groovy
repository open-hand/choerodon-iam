package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_uom_type_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_uom_type_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_uom_type_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_uom_type_tl", remarks: "计量单位类型多语言") {
            column(name: "uom_type_id", type: "bigint(20)",  remarks: "单位类型ID,hmdm_uom_type.uom_type_id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "uom_type_name", type: "varchar(" + 60 * weight + ")",  remarks: "单位类型名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"uom_type_id,lang",tableName:"hpfm_uom_type_tl",constraintName: "hpfm_uom_type_tl_u1")
    }
}