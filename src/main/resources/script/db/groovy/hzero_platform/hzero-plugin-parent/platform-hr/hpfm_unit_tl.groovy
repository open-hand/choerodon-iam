package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_unit_tl.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_unit_tl") {
		if(helper.dbType().isSupportSequence()){
			createSequence(sequenceName: 'hpfm_unit_tl_s', startValue:"1")
		}
        createTable(tableName: "hpfm_unit_tl", remarks: "部门多语言表") {
            column(name: "unit_id", type: "bigint(20)",  remarks: "上级部门ID,hpfm_hr_unit.unit_id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "unit_name", type: "varchar(" + 120 * weight + ")",  remarks: "部门名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"unit_id,lang",tableName:"hpfm_unit_tl",constraintName: "hpfm_unit_tl_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-04-23-hpfm_unit_tl") {
        addColumn(tableName: "hpfm_unit_tl"){
            column(name: "quick_index", type: "varchar(" + 30 * weight + ")",  remarks: "快速检索")
        }
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-22-hpfm_unit_tl") {
        modifyDataType(tableName: "hpfm_unit_tl", columnName: 'quick_index', newDataType: "varchar(" + 240 * weight + ")")
    }

}
