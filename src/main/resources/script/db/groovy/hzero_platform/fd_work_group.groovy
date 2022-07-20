package script.db.groovy.agile_service

/**
 *
 * @author zhaotianxin
 * @date 2021-05-07 11:43
 */
databaseChangeLog(logicalFilePath:'fd_work_group.groovy') {
    changeSet(id: '2021-11-08-fd-work-group', author: 'ztxemail@163.com') {
        createTable(tableName: "fd_work_group") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '工作组名称') {
                constraints(nullable: false)
            }
            column(name: 'parent_id', type: 'BIGINT UNSIGNED', remarks: '父级工作组id') {
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(255)', remarks: '排序字段')
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "fd_work_group", indexName: "idx_organization_id") {
            column(name: 'organization_id')
        }

        createIndex(tableName: "fd_work_group", indexName: "idx_parent_id") {
            column(name: 'parent_id')
        }
    }

    changeSet(id: '2022-04-28-fd-work-group', author: 'scp') {
        addColumn(tableName: 'fd_work_group') {
            column(name: 'open_object_id', type: 'VARCHAR(32)', remarks: '第三方对象Id 暂时用于三方同步',afterColumn: 'organization_id')
            column(name: 'open_type', type: 'VARCHAR(20)', remarks: '类型 暂时用于三方同步',afterColumn: 'open_object_id')
        }
    }
}
