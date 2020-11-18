package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_quick_link.groovy') {
    changeSet(author: 'wh', id: '2020-06-11-fd-quick-link') {
        createTable(tableName: "fd_quick_link") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REPORT')
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '快速链接名称')
            column(name: 'link_url', type: 'VARCHAR(1000)', remarks: '连接地址')
            column(name: 'create_user_id', type: 'BIGINT UNSIGNED', remarks: '创建用户id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'scope', type: 'VARCHAR(128)', remarks: '描述 self 自己可见 project 项目下可见')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "fd_quick_link", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "fd_quick_link", indexName: "idx_user_id") {
            column(name: "create_user_id")
        }
    }
    changeSet(author: 'wanghao', id: '2020-08-07-fd-quick-link-add-column') {
        addColumn(tableName: 'fd_quick_link') {
            column(name: 'top', type: "TINYINT UNSIGNED", remarks: '是否置顶', defaultValue: "0", afterColumn: 'scope')
        }
    }
    changeSet(author: 'wanghao', id: '2020-11-18-fd-quick-link-update-column') {
        renameColumn(columnDataType: 'TINYINT UNSIGNED', newColumnName: 'top_flag', oldColumnName: 'top', tableName: 'fd_quick_link')

    }
}