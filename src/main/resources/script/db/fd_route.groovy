package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_route.groovy') {

    changeSet(author: "xausky", id: "2019-09-10-add-fd-route") {
        createTable(tableName: "FD_ROUTE", remarks: '服务路由表') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_ROUTE')
            }
            column(name: 'SERVICE_CODE', type: 'VARCHAR(64)', remarks: '服务编码') {
                constraints(nullable: false)
            }
            column(name: "BACKEND_PATH", type: "varchar(240)", remarks: "后端路由")
            column(name: "FRONTEND_PATH", type: "varchar(240)", remarks: "前端路由")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_ROUTE', columnNames: 'SERVICE_CODE', constraintName: 'UK_FD_ROUTE_U1')
    }

    changeSet(id: '2019-10-26-fd-route-add-column', author: 'zongw.lee@gmail.com') {
        addColumn(tableName: 'fd_route') {
            column(name: 'STRIP_PREFIX', type: 'INT UNSIGNED', remarks: '是否去前缀')
            column(name: 'SENSITIVE_HEADERS', type: 'TEXT', remarks: '敏感头部列表')
            column(name: 'ROUTE_ID', type: 'VARCHAR(64)', remarks: '路由ID') {
                constraints(nullable: false)
            }
        }
    }
}