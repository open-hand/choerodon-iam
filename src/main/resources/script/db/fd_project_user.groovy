package script.db

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
            addUniqueConstraint(tableName: 'FD_PROJECT_USER', columnNames: 'MEMBER_ID, PROJECT_ID', constraintName: 'UK_FD_PROJECT_USER_U1')
        }
    }

}