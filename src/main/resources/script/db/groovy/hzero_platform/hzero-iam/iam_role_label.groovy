package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_label.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_role_label") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_role_label_s', startValue:"1")
        }
        createTable(tableName: "iam_role_label", remarks: "") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "role_id", type: "bigint(20)",  remarks: "角色的id")  {constraints(nullable:"false")}  
            column(name: "label_id", type: "bigint(20)",  remarks: "label的id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"role_id,label_id",tableName:"iam_role_label",constraintName: "iam_role_label_u1")
    }
}