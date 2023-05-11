package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_open_app_history.groovy') {
    changeSet(author: "changping.shi@hand-china.com", id: "2021-09-03-oauth_open_app_history") {
        createTable(tableName: "oauth_open_app_history", remarks: "三方同步历史") {
            column(name: "id", type: "BIGINT UNSIGNED", autoIncrement: true, remarks: "") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "BIGINT UNSIGNED", defaultValue: "0", remarks: "租户id，hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
            column(name: "open_app_id", type: "BIGINT UNSIGNED", remarks: "三方应用 ID") {
                constraints(nullable: "false")
            }
            column(name: "new_user_count", type: "int", remarks: "同步用户新增数量")
            column(name: "update_user_count", type: "int", remarks: "同步用户更新数量")
            column(name: "error_user_count", type: "int", remarks: "同步用户失败数量")
            column(name: "sync_begin_time", type: "datetime", remarks: "同步开始时间")
            column(name: "sync_end_time", type: "datetime", remarks: "同步结束时间")
            column(name: 'sync_type', type: "varchar(30)", defaultValue: "M", remarks: '同步类型(A-自动同步/M-手动同步)') {
                constraints(nullable: "false")
            }
            column(name: 'sync_status_flag', type: 'tinyint(3)', defaultValue: '0', remarks: '同步状态(0:同步中；1:同步完成)')
            column(name: 'error_log', type: "text", remarks: '异常日志')
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "更新时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
        }
        createIndex(indexName: "idx_tenant_app_id", tableName: "oauth_open_app_history") {
            column(name: "tenant_id")
            column(name: 'open_app_id')
        }
    }

    changeSet(author: 'changping.shi@hand-china.com', id: '2022-04-28_add_column') {
        addColumn(tableName: 'oauth_open_app_history') {
            column(name: "success_dept_count", type: "int", remarks: "同步成功的部门数量")
            column(name: "error_dept_count", type: "int", remarks: "同步失败部门数量")
            column(name: "sync_object_type", type: "varchar(30)", defaultValue: "U", remarks: "同步对象类型(U-用户/D-部门)")
        }
    }


    changeSet(author: 'changping.shi@hand-china.com', id: '2022-05-24_add_column') {
        addColumn(tableName: 'oauth_open_app_history') {
            column(name: "dis_mission_user_count", type: "int", remarks: "同步离职用户数")
        }
    }
}