package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_client_resource.groovy') {
    changeSet(author: 'wh', id: '2020-6-1-oauth-client-resource') {
        createTable(tableName: "oauth_client_resource", remarks: "客户端资源表") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_OAUTH_LDAP_AUTO')
            }
            column(name: 'client_id', type: 'BIGINT UNSIGNED', remarks: '客户端id') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_OAUTH_CLIENT_U1')
            }
            column(name: 'source_id', type: 'BIGINT UNSIGNED', remarks: '资源id')
            column(name: 'source_type', type: 'VARCHAR(10)', remarks: '资源类型')
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}