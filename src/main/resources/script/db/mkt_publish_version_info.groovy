package script.db
/**
 *  mkt_publish_version_info.groovy
 * 市场应用版本信息表（id,application_version_id,publish_app_id,status,changelog,document,approve_message,publish_date,publish_error_code）
 */
databaseChangeLog(logicalFilePath: 'script/db/mkt_publish_version_info.groovy') {
    changeSet(id: '2019-09-10-mkt_publish_version_info', author: 'longhe1996@icloud.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_PUBLISH_VERSION_INFO_S', startValue: "1")
        }
        createTable(tableName: "MKT_PUBLISH_VERSION_INFO", remarks: "市场应用版本表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '主键', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: "PK_MKT_PUBLISH_VERSION_INFO")
            }
            column(name: "APPLICATION_VERSION_ID", type: "BIGINT UNSIGNED", remarks: '应用版本ID') {
                constraints(nullable: false)
                constraints(unique: true, uniqueConstraintName: 'UK_MKT_PUBLISH_VERSION_INFO_U1')
            }
            column(name: 'PUBLISH_APPLICATION_ID', type: 'BIGINT UNSIGNED', remarks: '市场应用ID') {
                constraints(nullable: false)
            }
            column(name: 'STATUS', type: 'VARCHAR(20)', defaultValue: "unpublished", remarks: '版本状态。' +
                    'unpublished(默认):未发布;' +
                    'withdrawn:已撤销;' +
                    'rejected:被驳回;' +
                    'under_approval:审批中;' +
                    'unconfirmed:待最后确认的;' +
                    'published:已发布') {
                constraints(nullable: false)
            }
            column(name: 'REMARK', type: 'VARCHAR(255)', remarks: '版本申请备注')
            column(name: 'TIMES_OF_FIXES', type: 'INT UNSIGNED', remarks: '发布修复版本的次数', defaultValue: 0) {
                constraints(nullable: false)
            }
            column(name: 'CHANGELOG', type: 'TEXT', remarks: '版本日志')
            column(name: 'DOCUMENT', type: 'TEXT', remarks: '文档')
            column(name: 'APPROVE_MESSAGE', type: 'VARCHAR(255)', remarks: '审批信息')
            column(name: "PUBLISH_DATE", type: "DATETIME", remarks: '版本的发布时间')
            column(name: 'PUBLISH_ERROR_CODE', type: 'VARCHAR(255)', remarks: '发布错误编码(仅在发布失败时有值)')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}
