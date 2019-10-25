package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-03-iam-role-permission') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_ROLE_PERMISSION_S', startValue:"1")
        }
        createTable(tableName: "IAM_ROLE_PERMISSION") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_ROLE_PERMISSION')
            }
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色id')
            column(name: 'PERMISSION_ID', type: 'BIGINT UNSIGNED', remarks: '权限id')
        }
        addUniqueConstraint(tableName: 'IAM_ROLE_PERMISSION', columnNames: 'ROLE_ID, PERMISSION_ID', constraintName: 'UK_IAM_ROLE_PERM_U1')
    }

    changeSet(author: 'superlee', id: '2019-04-16-role-permission-upgrade') {
        addColumn(tableName: 'IAM_ROLE_PERMISSION') {
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superlee', id: '2019-07-18-iam-role-permission-add-remark') {
        setTableRemarks(tableName:"IAM_ROLE_PERMISSION",remarks: "角色权限关系表")
    }

    changeSet(author: 'xausky', id: '2018-08-05-iam-role-permission-code-relation') {
        renameColumn(tableName:'IAM_ROLE_PERMISSION',oldColumnName:'ROLE_ID',newColumnName:'ROLE_CODE',columnDataType:'VARCHAR(128)',remarks:'角色代码')
        renameColumn(tableName:'IAM_ROLE_PERMISSION',oldColumnName:'PERMISSION_ID',newColumnName:'PERMISSION_CODE',columnDataType:'VARCHAR(128)',remarks:'权限代码')
    }
}
