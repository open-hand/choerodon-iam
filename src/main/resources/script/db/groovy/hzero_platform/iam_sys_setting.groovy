package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_sys_setting.groovy') {
    changeSet(author: 'qiang.zeng', id: '2019-08-29-iam-sys-setting') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_SYS_SETTING_S', startValue: "1")
        }
        createTable(tableName: "IAM_SYS_SETTING", remarks: "系统配置表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_SYS_SETTING')
            }
            column(name: 'SETTING_KEY', type: 'VARCHAR(255)', remarks: '配置属性') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_IAM_SYS_SETTING_U1')
            }
            column(name: 'SETTING_VALUE', type: 'VARCHAR(255)', remarks: '配置属性值')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

}