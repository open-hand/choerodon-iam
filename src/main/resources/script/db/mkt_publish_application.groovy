package script.db
/**
 * mkt_publish_application.groovy
 * 市场发布应用信息表 (id,ref_app_id,is_released,description,image_url,
 * publish_type,is_free,remark,overview,
 * category_name,category_code,
 * latest_version)
 */
databaseChangeLog(logicalFilePath: 'script/db/mkt_publish_application.groovy') {
    changeSet(author: 'longhe6699@gmail.com', id: '2019-09-10-mkt_publish_application') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_PUBLISH_APPLICATION_S', startValue: "1")
        }
        createTable(tableName: "MKT_PUBLISH_APPLICATION", remarks: "市场应用表，用于记录已发布的市场应用") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_MKT_PUBLISH_APPLICATION')
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '市场发布名称') {
                constraints(nullable: false)
            }
            column(name: "REF_APP_ID", type: 'BIGINT UNSIGNED', remarks: '关联应用主键（fd_application.id）') {
                constraints(nullable: false)
            }
            column(name: "IS_RELEASED", type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否已发布。0：尚未发布，默认；1：已发布。') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '市场应用描述') {
                constraints(nullable: false)
            }
            column(name: 'IMAGE_URL', type: 'VARCHAR(255)', remarks: '市场应用图标URL') {
                constraints(nullable: false)
            }
            column(name: 'CONTRIBUTOR', type: 'VARCHAR(100)', remarks: '贡献者，一般为组织名') {
                constraints(nullable: false)
            }
            column(name: 'NOTIFICATION_EMAIL', type: 'VARCHAR(128)', remarks: '通知邮箱') {
                constraints(nullable: false)
            }
            column(name: 'PUBLISH_TYPE', type: 'VARCHAR(32)', defaultValue: 'mkt_deploy_only', remarks: '发布类型：' +
                    'mkt_deploy_only(默认) : 仅可部署;' +
                    'mkt_code_only : 仅可下载;' +
                    'mkt_code_deploy : 可下载也可部署') {
                constraints(nullable: false)
            }

            column(name: "IS_FREE", type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否免费。1(默认):免费,0:收费。') {
                constraints(nullable: false)
            }
            column(name: 'OVERVIEW', type: 'TEXT', remarks: '市场应用详细介绍')
            column(name: 'CATEGORY_CODE', type: 'VARCHAR(64)', remarks: '市场应用类别编码')
            column(name: 'CATEGORY_NAME', type: 'VARCHAR(50)', remarks: '市场应用类别名称')
            column(name: "LATEST_VERSION_ID", type: 'BIGINT UNSIGNED', remarks: '最新应用版本主键。' +
                    'IS_RELEASED=1时，即记录已发布，则表示最近发布版本；' +
                    'IS_RELEASED=0时，即记录尚未发布，则表示对应的版本。')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        addUniqueConstraint(tableName: 'MKT_PUBLISH_APPLICATION', columnNames: 'REF_APP_ID,IS_RELEASED', constraintName: 'UK_MKT_PUBLISH_APPLICATION_U1')

    }
}