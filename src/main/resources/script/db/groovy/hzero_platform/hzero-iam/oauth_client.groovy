package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_client.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_client") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_client_s', startValue:"1")
        }
        createTable(tableName: "oauth_client", remarks: "") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "name", type: "varchar(" + 32 * weight + ")",  remarks: "客户端名称")  {constraints(nullable:"false")}
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织ID")  {constraints(nullable:"false")}
            column(name: "resource_ids", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "资源ID列表，目前只使用default")
            column(name: "secret", type: "varchar(" + 255 * weight + ")",  remarks: "客户端秘钥")
            column(name: "scope", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "Oauth授权范围")
            column(name: "authorized_grant_types", type: "varchar(" + 255 * weight + ")",  remarks: "支持的授权类型列表")
            column(name: "web_server_redirect_uri", type: "varchar(" + 128 * weight + ")",  remarks: "授权重定向URL")
            column(name: "access_token_validity", type: "bigint(20)",  remarks: "客户端特定的AccessToken超时时间")
            column(name: "refresh_token_validity", type: "bigint(20)",  remarks: "客户端特定的RefreshToken超时时间")
            column(name: "additional_information", type: "varchar(" + 1024 * weight + ")",  remarks: "客户端附加信息")
            column(name: "auto_approve", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "自动授权范围列表")
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "")
            column(name: "created_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"0",   remarks: "")
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")

        }

        addUniqueConstraint(columnNames:"name",tableName:"oauth_client",constraintName: "oauth_client_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-08-06-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "enabled_flag", type: "tinyint(1)", defaultValue:"1", remarks: "启用标识")  {constraints(nullable:"false")}
        }
    }
}