package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_role_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_role_tl_s', startValue:"1")
        }
        createTable(tableName: "iam_role_tl", remarks: "") {
            column(name: "lang", type: "varchar(" + 8 * weight + ")",  remarks: "语言code")  {constraints(nullable:"false")}  
            column(name: "id", type: "bigint(20)",  remarks: "role表id")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 64 * weight + ")",  remarks: "多语言字段")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"iam_role_tl",constraintName: "iam_role_tl_pk")
    }
}