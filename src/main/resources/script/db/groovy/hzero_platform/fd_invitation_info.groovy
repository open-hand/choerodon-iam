package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_invitation_info.groovy') {
    changeSet(author: 'hzero@163.com', id: '2020-05-19-fd-invitation-info') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_INVITATION_INFO_S', startValue: "1")
        }
        createTable(tableName: "FD_INVITATION_INFO") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_INVITATION_INFO')
            }
            column(name: 'EMAIL', type: 'VARCHAR(128)', remarks: '邀请用户邮箱') {
            }
            column(name: 'ORG_ID', type: 'BIGINT UNSIGNED', remarks: '邀请用户进入的组织') {
                constraints(nullable: false)
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: '邀请用户被分配了角色的项目id'){
                constraints(nullable: false)
            }

            column(name: 'ROLE_ID',  type: 'VARCHAR(64)', remarks: '邀请用户被分配的角色id集合') {
                constraints(nullable: false)
            }

            column(name: 'URL_TOKEN',  type: 'VARCHAR(64)', remarks: '链接token') {
                constraints(nullable: false)
            }
            column(name: 'URL_END_DATE',  type: 'DATETIME', remarks: '链接到期时间') {
                constraints(nullable: false)
            }
            column(name: 'IS_ENABLED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用。1启用，0未启用') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

}