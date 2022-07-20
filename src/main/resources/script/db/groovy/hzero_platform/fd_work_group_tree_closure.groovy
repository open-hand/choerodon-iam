package script.db.groovy.agile_service

databaseChangeLog(logicalFilePath: 'script/db/fd_work_group_tree_closure.groovy') {
    changeSet(id: '2021-11-10-fd-work-group-tree-closure', author: 'ztxemail@163.com') {
        createTable(tableName: "fd_work_group_tree_closure", remarks: '工作组闭包表，工作组树形结构') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'ancestor_id', type: 'BIGINT UNSIGNED', remarks: '祖先id') {
                constraints(nullable: false)
            }
            column(name: 'descendant_id', type: 'BIGINT UNSIGNED', remarks: '后代id') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}