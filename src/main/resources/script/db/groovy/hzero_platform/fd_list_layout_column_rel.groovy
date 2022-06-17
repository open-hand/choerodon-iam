package script.db.groovy.hzero_platform

/**
 *
 * @author lihao
 * @date 2022-06-17 11:48
 * */
databaseChangeLog(logicalFilePath:'fd_list_layout_column_rel.groovy') {
    changeSet(id: '2022-06-17-fd-list-layout-column-rel', author: 'lihao') {
        createTable(tableName: "fd_list_layout_column_rel") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'layout_id', type: 'BIGINT UNSIGNED', remarks: '布局id') {
                constraints(nullable: false)
            }

            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id')

            column(name: 'column_code', type: 'VARCHAR(100)', remarks: '列编码') {
                constraints(nullable: false)
            }
            column(name: 'display', type: 'TINYINT UNSIGNED', remarks: '是否显示') {
                constraints(nullable: false)
            }
            column(name: 'width', type: 'int', remarks: '列的宽度') {
                constraints(nullable: false)
            }
            column(name: 'sort', type: 'int', remarks: '排序') {
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
    }

    changeSet(id: '2022-04-01-fd-list-layout-column-rel-add-column', author: 'huaxin.deng@hand-china.com') {
        addColumn(tableName: 'fd_list_layout_column_rel') {
            column(name: 'extra_config', type: 'TINYINT UNSIGNED(1)', remarks: '额外配置')
        }
    }
}
