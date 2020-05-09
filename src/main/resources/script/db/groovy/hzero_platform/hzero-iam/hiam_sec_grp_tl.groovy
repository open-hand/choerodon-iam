package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_tl.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_tl_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_tl", remarks: "安全组多语言表") {
            column(name: "sec_grp_id", type: "bigint(20)",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "sec_grp_name", type: "varchar(" + 255 * weight + ")",  remarks: "安全组名称")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   

        }

        addUniqueConstraint(columnNames:"sec_grp_id,lang",tableName:"hiam_sec_grp_tl",constraintName: "hiam_sec_grp_tl_u1")
    }
}