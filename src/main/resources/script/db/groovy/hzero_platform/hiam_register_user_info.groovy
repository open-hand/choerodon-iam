package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_register_user_info.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2020-05-23-hiam-register-user-info') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HIAM_REGISTER_USER_INFO_S', startValue: "1")
        }
        createTable(tableName: "HIAM_REGISTER_USER_INFO") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_HIAM_REGISTER_USER_INFO')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: '用户ID') {
            }
            column(name: 'USER_NAME', type: 'VARCHAR(128)', remarks: '用户名字') {
            }
            column(name: 'ORGANIZATION_NAME', type: 'VARCHAR(32)', remarks: '组织名字')
            column(name: 'ORGANIZATION_HOME_PAGE', type: 'VARCHAR(255)', remarks: '组织官网地址') {
            }

            column(name: 'ORGANIZATION_POSITION', type: 'VARCHAR(50)', remarks: '组织职位') {
            }
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
}