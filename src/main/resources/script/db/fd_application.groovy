package script.db

/**
 * fd_application.groovy
 * 应用表 (id,project_id,name,code,type,description,source_uuid,feedback_token)
 */
databaseChangeLog(logicalFilePath: 'script/db/fd_application.groovy') {
    changeSet(author: 'zongw.lee@gmail.com', id: '2019-09-10-fd_application') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_APPLICATION_S', startValue: "1")
        }
        createTable(tableName: "FD_APPLICATION", remarks: "应用表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_APPLICATION')
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: '所属项目ID') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '应用名称') {
                constraints(nullable: false)
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '应用编码(自动生成的UUID)') {
                constraints(nullable: false)
            }
            column(name: 'TYPE', type: 'VARCHAR(32)', remarks: '应用类别：' +
                    'custom(自定义的应用),' +
                    'mkt_code_base(应用市场下载且包含代码库的应用)' +
                    'mkt_deploy_only(应用市场下载且只能部署的应用)' +
                    'mkt_code_deploy(应用市场下载有代码库并且能部署的应用)') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '应用描述')

            column(name: 'SOURCE_CODE', type: 'VARCHAR(64)', remarks: '应用来源的UUID（自定义、应用市场下载）')

            column(name: 'FEEDBACK_TOKEN', type: 'VARCHAR(64)', remarks: 'FEEDBACK使用的应用TOKEN(自动生成的UUID)') {
                constraints(unique: true, uniqueConstraintName: 'UK_FD_APPLICATION_U1')
                constraints(nullable: false)
            }
            column(name: 'HAS_GENERATED', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否已生成市场发布信息。' +
                    '0(默认):没有生成市场发布信息;' +
                    '1:已生成市场发布信息。') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_APPLICATION', columnNames: 'CODE,PROJECT_ID', constraintName: 'UK_FD_APPLICATION_U2')
        addUniqueConstraint(tableName: 'FD_APPLICATION', columnNames: 'NAME,PROJECT_ID', constraintName: 'UK_FD_APPLICATION_U3')
    }
}