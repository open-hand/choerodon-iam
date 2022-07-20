package script.db.groovy.agile_service

/**
 *
 * @author zhaotianxin* @date 2021-11-08 11:43
 */
databaseChangeLog(logicalFilePath: 'fd_work_group_project_rel.groovy') {
    changeSet(id: '2021-11-08-fd-work-group-user-rel', author: 'ztxemail@163.com') {
        createTable(tableName: "fd_work_group_project_rel") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键id') {
                constraints(primaryKey: true)
            }
            column(name: 'work_group_id', type: 'BIGINT UNSIGNED', remarks: '工作组id') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
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

        createIndex(tableName: "fd_work_group_project_rel", indexName: "idx_organization_id") {
            column(name: 'organization_id')
        }

        createIndex(tableName: "fd_work_group_project_rel", indexName: "idx_work_group_id") {
            column(name: 'work_group_id')
        }

        createIndex(tableName: "fd_work_group_project_rel", indexName: "idx_project_id") {
            column(name: 'project_id')
        }
    }
}
