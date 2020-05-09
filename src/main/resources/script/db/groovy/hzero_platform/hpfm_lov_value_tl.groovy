package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_value_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov_value_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_value_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov_value_tl", remarks: "LOV独立值集多语言表") {
            column(name: "lov_value_id", type: "bigint(20)",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "meaning", type: "varchar(" + 120 * weight + ")",  remarks: "含义")   
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"lov_value_id,lang",tableName:"hpfm_lov_value_tl",constraintName: "hpfm_lov_value_tl_u1")
    }
}