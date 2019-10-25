package script.db

databaseChangeLog(logicalFilePath: 'script/db/mkt_app_organization_ref.groovy') {
    changeSet(author: 'zongw.lee@gmail.com', id: '2019-09-21-mkt-app-organization-ref') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'MKT_APP_ORGANIZATION_REF_S', startValue: "1")
        }
        createTable(tableName: "MKT_APP_ORGANIZATION_REF", remarks: "下载到本地的应用与组织关联") {
            column(name: 'APP_ID', type: 'BIGINT UNSIGNED', remarks: '本地应用Id') {
                constraints(nullable: false)
            }
            column(name: 'APP_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '本地应用版本Id') {
                constraints(nullable: false)
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织ID') {
                constraints(nullable: false)
            }
            column(name: 'MKT_VERSION_ID', type: 'BIGINT UNSIGNED', remarks: '市场应用版本Id') {
                constraints(nullable: false)
            }
        }
    }
}
