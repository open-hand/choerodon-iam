package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_menu_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_menu_permission_s', startValue:"1")
        }
        createTable(tableName: "iam_menu_permission", remarks: "") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "menu_id", type: "bigint(20)",  remarks: "菜单id")   
            column(name: "permission_code", type: "varchar(" + 128 * weight + ")",  remarks: "权限的标识")   

        }

        addUniqueConstraint(columnNames:"menu_id,permission_code",tableName:"iam_menu_permission",constraintName: "iam_menu_permission_u1")
    }
}