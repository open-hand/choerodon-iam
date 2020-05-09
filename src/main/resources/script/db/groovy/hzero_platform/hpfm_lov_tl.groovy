package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov_tl", remarks: "LOV多语言表") {
            column(name: "lov_id", type: "bigint(20)",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "lov_name", type: "varchar(" + 240 * weight + ")",  remarks: "值集名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"lov_id,lang",tableName:"hpfm_lov_tl",constraintName: "hpfm_lov_tl_u1")
    }
}