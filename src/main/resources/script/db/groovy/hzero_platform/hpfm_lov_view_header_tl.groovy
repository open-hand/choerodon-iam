package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_view_header_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-08-27-hpfm_lov_view_header_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_view_header_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov_view_header_tl", remarks: "值集视图头多语言") {
            column(name: "view_header_id", type: "bigint(20)", remarks: "值集视图头Id，hpfm_lov_view_header.view_header_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}
            column(name: "view_name", type: "varchar(" + 240 * weight + ")",  remarks: "视图名称")   
            column(name: "title", type: "varchar(" + 60 * weight + ")",  remarks: "标题")
        }

        addUniqueConstraint(columnNames:"view_header_id,lang",tableName:"hpfm_lov_view_header_tl",constraintName: "hpfm_lov_view_header_tl_u1")
    }
}
