package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_ui_permission_comp.groovy') {
    changeSet(author: "jianbo.li@hand-chian.com", id: "2019-07-31-hiam_ui_permission_comp") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_ui_permission_comp_s', startValue:"1")
        }
        createTable(tableName: "hiam_ui_permission_comp", remarks: "UI权限组件数据") {
            column(name: "ui_permission_comp_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "page_route", type: "varchar(" + 128 * weight + ")",  remarks: "页面主路由")  {constraints(nullable:"false")}  
            column(name: "permission_code", type: "varchar(" + 128 * weight + ")",  remarks: "权限代码")  {constraints(nullable:"false")}  
            column(name: "permission_name", type: "varchar(" + 128 * weight + ")",  remarks: "权限名称")   
            column(name: "permission_type", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型")  {constraints(nullable:"false")}  

        }
		createIndex(tableName: "hiam_ui_permission_comp", indexName: "hiam_ui_permission_comp_n1") {
            column(name: "page_route")
        }

    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2019-12-30-hiam_ui_permission_comp') {
		def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
		modifyDataType(tableName: "hiam_ui_permission_comp", columnName: 'permission_code', newDataType: "varchar(" + 255 * weight + ")")
    }
}