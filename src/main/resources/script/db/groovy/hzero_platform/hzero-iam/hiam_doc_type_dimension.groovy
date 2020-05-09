package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_dimension.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-09-29-hiam_doc_type_dimension") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_dimension_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type_dimension", remarks: "单据类型维度") {
            column(name: "dimension_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "dimension_code", type: "varchar(" + 30 * weight + ")",  remarks: "维度编码")  {constraints(nullable:"false")}  
            column(name: "dimension_name", type: "varchar(" + 60 * weight + ")",  remarks: "维度名称")  {constraints(nullable:"false")}  
            column(name: "dimension_type", type: "varchar(" + 30 * weight + ")",  remarks: "维度类型，值集：HIAM.AUTHORITY_SCOPE_CODE")  {constraints(nullable:"false")}  
            column(name: "value_source_type", type: "varchar(" + 30 * weight + ")",  remarks: "值来源类型，值集：HIAM.DOC_DIMENSION.SOURCE_TYPE")  {constraints(nullable:"false")}  
            column(name: "value_source", type: "varchar(" + 30 * weight + ")",  remarks: "值来源")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint(1)",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int(11)",   defaultValue:"0",   remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"dimension_code",tableName:"hiam_doc_type_dimension",constraintName: "hiam_doc_type_dimension_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-05-hiam_doc_type_dimension") {
        dropNotNullConstraint(tableName: "hiam_doc_type_dimension", columnName: "value_source_type", columnDataType: "varchar(" + 30 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-06-hiam_doc_type_dimension") {
        dropNotNullConstraint(tableName: "hiam_doc_type_dimension", columnName: "value_source", columnDataType: "varchar(" + 30 * weight + ")")
    }
}