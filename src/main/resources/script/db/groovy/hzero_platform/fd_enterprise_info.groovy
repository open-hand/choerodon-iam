package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_enterprise_info.groovy') {
    changeSet(author: 'wanghao', id: '2020-11-04-fd_enterprise_info') {
        createTable(tableName: "fd_enterprise_info", remarks: "记录用户开源信息表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'ORGANIZATION_NAME', type: 'VARCHAR(255)', remarks: '组织名称') {
                constraints(nullable: false)
            }

            column(name: 'ADMIN_NAME', type: 'VARCHAR(128)', remarks: 'admin姓名') {
                constraints(nullable: false)
            }

            column(name: 'ADMIN_PHONE', type: 'VARCHAR(32)', remarks: '管理员手机号') {
                constraints(nullable: false)
            }
            column(name: 'ADMIN_EMAIL', type: 'VARCHAR(128)', remarks: '管理员邮箱地址') {
                constraints(nullable: false)
            }
            column(name: 'ENTERPRISE_NAME', type: 'VARCHAR(255)', remarks: '公司名称') {
                constraints(nullable: false)
            }
            column(name: 'ENTERPRISE_SCALE', type: 'VARCHAR(50)', remarks: '公司规模') {
                constraints(nullable: false)
            }
            column(name: 'ENTERPRISE_TYPE', type: 'VARCHAR(50)', remarks: '行业') {
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