package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_custom_layout_config.groovy') {
    changeSet(author: 'wanghao', id: '2021-01-06-fd_custom_layout_config') {
        createTable(tableName: "fd_custom_layout_config", remarks: "用户自定义面板布局表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'SOURCE_TYPE', type: 'VARCHAR(128)', remarks: '配置类型') {
                constraints(nullable: false)
            }

            column(name: 'SOURCE_ID', type: 'BIGINT UNSIGNED', remarks: '配置关联对象id') {
                constraints(nullable: false)
            }

            column(name: 'DATA', type: 'TEXT', remarks: '配置数据') {
                constraints(nullable: false)
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