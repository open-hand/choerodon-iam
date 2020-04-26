package script.db.groovy.hzero_platform

databaseChangeLog(logicalFilePath: 'script/db/fd_project_user.groovy') {
    changeSet(author: 'scp', id: '2020-04-16-fd-project-user') {
        createTable(tableName: "FD_PROJECT_USER") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_PROJECT')
            }
            column(name: 'MEMBER_ID', type: 'BIGINT UNSIGNED', remarks: '用户id') {
                constraints(nullable: false)
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'zmf', id: 'FD_PROJECT_USER_ADD_ROLE_ID_AND_AUDIT_DOMAIN') {
        addColumn(tableName: "FD_PROJECT_USER") {
            column(name: 'ROLE_ID', type: 'BIGINT UNSIGNED', remarks: '角色id') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        addUniqueConstraint(tableName: 'FD_PROJECT_USER', columnNames: 'PROJECT_ID, MEMBER_ID, ROLE_ID', constraintName: 'UK_FD_PROJECT_USER_ROLE')
    }
}