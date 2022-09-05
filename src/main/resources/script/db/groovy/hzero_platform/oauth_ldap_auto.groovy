package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_ldap_auto.groovy') {
    changeSet(author: 'scp', id: '2019-11-21-oauth-ldap-auto') {
        createTable(tableName: "OAUTH_LDAP_AUTO", remarks: "ldap自动同步表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LDAP_AUTO')
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_LDAP_AUTO_U1')
            }
            column(name: 'FREQUENCY', type: 'VARCHAR(10)', remarks: '同步频率') {
                constraints(nullable: false)
            }
            column(name: "START_TIME", type: "DATETIME", remarks: '开始时间') {
                constraints(nullable: false)
            }
            column(name: "QUARTZ_TASK_ID", type: "BIGINT UNSIGNED", remarks: '定时任务Id')

            column(name: "ACTIVE", type: "TINYINT UNSIGNED", remarks: '是否启用', defaultValue: "1")

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