package script.db

/**
 * fd_route_rule.groovy
 * 路由规则表 (id,name,code,description)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_route_rule.groovy') {
    changeSet(author: 'relaxingchu@qq.com', id: '2019-10-25-add_table_fd_route_rule') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_ROUTE_RULE_S', startValue: "1")
        }
        createTable(tableName: "FD_ROUTE_RULE", remarks: "路由规则表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_ROUTE_RULE')
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '路由名称') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_FD_ROUTE_RULE_U1')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '路由编码(自动生成的UUID)') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_FD_ROUTE_RULE_U2')
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '路由描述')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}