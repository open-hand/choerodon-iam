package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_clause_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_clause_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_clause_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_clause_tl", remarks: "工作台条目多语言") {
            column(name: "clause_id", type: "bigint(20)",  remarks: "工作台条目ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "clause_name", type: "varchar(" + 60 * weight + ")",  remarks: "条目名称")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"clause_id,lang",tableName:"hpfm_dashboard_clause_tl",constraintName: "hpfm_dashboard_clause_tl_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-03-11-hpfm_dashboard_clause_tl") {
        if (helper.isSqlServer()) {
            dropDefaultValue(tableName: 'hpfm_dashboard_clause_tl', columnName: 'creation_date')
            dropDefaultValue(tableName: 'hpfm_dashboard_clause_tl', columnName: 'created_by')
            dropDefaultValue(tableName: 'hpfm_dashboard_clause_tl', columnName: 'last_updated_by')
            dropDefaultValue(tableName: 'hpfm_dashboard_clause_tl', columnName: 'last_update_date')
            dropNotNullConstraint(tableName: "hpfm_dashboard_clause_tl", columnName: "creation_date", columnDataType: "datetime")
            dropNotNullConstraint(tableName: "hpfm_dashboard_clause_tl", columnName: "created_by", columnDataType: "bigint(20)")
            dropNotNullConstraint(tableName: "hpfm_dashboard_clause_tl", columnName: "last_updated_by", columnDataType: "bigint(20)")
            dropNotNullConstraint(tableName: "hpfm_dashboard_clause_tl", columnName: "last_update_date", columnDataType: "datetime")
        }
        dropColumn(tableName: "hpfm_dashboard_clause_tl", columnName: "creation_date")
        dropColumn(tableName: "hpfm_dashboard_clause_tl", columnName: "created_by")
        dropColumn(tableName: "hpfm_dashboard_clause_tl", columnName: "last_updated_by")
        dropColumn(tableName: "hpfm_dashboard_clause_tl", columnName: "last_update_date")
    }
}
