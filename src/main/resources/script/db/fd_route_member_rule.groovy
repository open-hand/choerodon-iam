package script.db

/**
 * fd_route_member_rule.groovy
 * 用户_路由规则表 (id，user_id，route_rule_code)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_route_member_rule.groovy') {
    changeSet(author: 'relaxingchu@qq.com', id: '2019-10-25-add-table-fd-route-member-rule') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_ROUTE_MEMBER_RULE_S', startValue: "1")
        }
        createTable(tableName: "FD_ROUTE_MEMBER_RULE", remarks: "用户路由规则表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_ROUTE_MEMBER_RULE')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: '用户ID') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_FD_ROUTE_MEMBER_RULE_U1')
            }
            column(name: 'ROUTE_RULE_CODE', type: 'VARCHAR(64)', remarks: '路由编码') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(indexName: "IDX_ROUTE_RULE_CODE", tableName: "FD_ROUTE_MEMBER_RULE") {
            column(name: "ROUTE_RULE_CODE")
        }
    }
}