package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_dcl_dim_line.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_dcl_dim_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_dcl_dim_line_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_dcl_dim_line", remarks: "安全组数据权限维度行") {
            column(name: "sec_grp_dcl_dim_line_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_dcl_dim_id", type: "bigint(20)",  remarks: "安全组数据权限维度ID")  {constraints(nullable:"false")}  
            column(name: "sec_grp_id", type: "bigint(20)",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "auth_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型代码，HIAM.AUTHORITY_TYPE_CODE")  {constraints(nullable:"false")}  
            column(name: "auto_assign_flag", type: "tinyint(1)",   defaultValue:"0",   remarks: "是否自动分配。1是，0否")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_dcl_dim_id,auth_type_code",tableName:"hiam_sec_grp_dcl_dim_line",constraintName: "hiam_sec_grp_dcl_dim_line_u1")
    }
}