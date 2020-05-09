package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_menu_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_menu_tl_s', startValue:"1")
        }
        createTable(tableName: "iam_menu_tl", remarks: "") {
            column(name: "lang", type: "varchar(" + 16 * weight + ")",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "id", type: "bigint(20)",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 64 * weight + ")",  remarks: "菜单名")   

        }

        addUniqueConstraint(columnNames:"lang,id",tableName:"iam_menu_tl",constraintName: "iam_menu_tl_pk")
    }
}