package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_custom_point_tl.groovy') {
    changeSet(author: "hzero", id: "2019-12-16-hiam_custom_point_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_custom_point_tl_s', startValue:"1")
        }
        createTable(tableName: "hiam_custom_point_tl", remarks: "客户化端点多语言表") {
            column(name: "custom_point_id", type: "bigint(20)", autoIncrement: true ,   remarks: "hiam_custom_point.custom_point_id")  {constraints(primaryKey: true)} 
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")  {constraints(nullable:"false")}     

        }

        addUniqueConstraint(columnNames:"custom_point_id,lang",tableName:"hiam_custom_point_tl",constraintName: "hiam_custom_point_tl_u1")
    }
}