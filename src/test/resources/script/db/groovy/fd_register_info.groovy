package script.db.groovy

databaseChangeLog(logicalFilePath: 'script/db/fd_register_info.groovy') {
    changeSet(author: 'qiang.zeng@hand-china.com', id: '2019-04-09-fd-register-info') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_REGISTER_INFO_S', startValue: "1")
        }
        createTable(tableName: "FD_REGISTER_INFO") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REGISTER_INFO')
            }
            column(name: 'USER_ID', type: 'BIGINT UNSIGNED', remarks: '用户主键ID')
            column(name: 'USER_NAME', type: 'VARCHAR(128)', remarks: '用户名')
            column(name: 'USER_PHONE', type: 'VARCHAR(32)', remarks: '用户手机号')
            column(name: 'USER_EMAIL', type: 'VARCHAR(128)', remarks: '用户电子邮箱地址')
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织主键ID')
            column(name: 'ORGANIZATION_NAME', type: 'VARCHAR(32)', remarks: '组织名称')
            column(name: 'PROJECT_COUNT', type: 'TINYINT UNSIGNED', remarks: '绑定项目数量')
            column(name: 'APPLICATION_COUNT', type: 'TINYINT UNSIGNED', remarks: '绑定应用数量')
            column(name: "REGISTER_DATE", type: "DATETIME", remarks: '注冊日期')
            column(name: "INVALID_DATE", type: "DATETIME", remarks: '到期日期')
            column(name: "LAST_LOGIN_AT", type: "DATETIME", remarks: '用户上次登录日期')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'qiang.zeng@hand-china.com', id: '2019-07-16-fd-register-info-delete') {
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'USER_NAME')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'USER_PHONE')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'USER_EMAIL')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'ORGANIZATION_ID')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'ORGANIZATION_NAME')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'PROJECT_COUNT')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'APPLICATION_COUNT')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'REGISTER_DATE')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'INVALID_DATE')
        dropColumn(tableName: 'FD_REGISTER_INFO', columnName: 'LAST_LOGIN_AT')
    }

    changeSet(author: 'qiang.zeng@hand-china.com', id: '2019-07-16-fd-register-info-add') {
        addColumn(tableName: 'FD_REGISTER_INFO') {
            column(name: 'USER_NAME', type: 'VARCHAR(128)', remarks: '用户名', afterColumn: 'USER_ID') {
                constraints(nullable: false)
            }
            column(name: 'USER_PHONE', type: 'VARCHAR(32)', remarks: '用户手机号', afterColumn: 'USER_NAME') {
                constraints(nullable: false)
            }
            column(name: 'USER_EMAIL', type: 'VARCHAR(128)', remarks: '用户电子邮箱地址', afterColumn: 'USER_PHONE') {
                constraints(nullable: false)
            }
            column(name: 'USER_ORG_NAME', type: 'VARCHAR(32)', remarks: '用户所属的组织名', afterColumn: 'USER_EMAIL')
            column(name: 'USER_ORG_HOME_PAGE', type: 'VARCHAR(255)', remarks: '用户所属的组织官网', afterColumn: 'USER_ORG_NAME')
            column(name: 'USER_ORG_POSITION', type: 'VARCHAR(50)', remarks: '用户所属的组织职位', afterColumn: 'USER_ORG_HOME_PAGE') {
                constraints(nullable: false)
            }
            column(name: 'USER_TOKEN', type: 'BLOB', remarks: '用户唯一token。用于用户修改密码时使用', afterColumn: 'USER_ORG_POSITION')
            column(name: 'ORG_NAME', type: 'VARCHAR(32)', remarks: '用户注册的组织名', afterColumn: 'USER_TOKEN') {
                constraints(nullable: false)
            }
            column(name: 'ORG_HOME_PAGE', type: 'VARCHAR(255)', remarks: '用户注册的组织官网', afterColumn: 'ORG_NAME') {
                constraints(nullable: false)
            }
            column(name: 'ORG_EMAIL_SUFFIX', type: 'VARCHAR(50)', remarks: '用户注册的组织邮箱后缀', afterColumn: 'ORG_HOME_PAGE') {
                constraints(nullable: false)
            }
            column(name: 'ORG_SCALE', type: 'INT UNSIGNED', remarks: '用户注册的组织规模', afterColumn: 'ORG_EMAIL_SUFFIX') {
                constraints(nullable: false)
            }
            column(name: 'ORG_BUSINESS_TYPE', type: 'VARCHAR(50)', remarks: '用户注册的组织所在行业', afterColumn: 'ORG_SCALE') {
                constraints(nullable: false)
            }
            column(name: 'REGISTER_DATE', type: 'DATETIME', remarks: '注册时间', afterColumn: 'ORG_BUSINESS_TYPE', defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: 'APPROVAL_STATUS', type: 'VARCHAR(15)', remarks: '审批状态。未审批，审批通过，审批拒绝', afterColumn: 'REGISTER_DATE')
            column(name: 'APPROVAL_MESSAGE', type: 'VARCHAR(255)', remarks: '审批原因，拒绝时填入', afterColumn: 'APPROVAL_STATUS')
            column(name: 'APPROVAL_USER_ID', type: 'BIGINT UNSIGNED', remarks: '审批人id', afterColumn: 'APPROVAL_MESSAGE')
            column(name: 'APPROVAL_DATE', type: 'DATETIME', remarks: '审批时间', afterColumn: 'APPROVAL_USER_ID')
        }
    }
}