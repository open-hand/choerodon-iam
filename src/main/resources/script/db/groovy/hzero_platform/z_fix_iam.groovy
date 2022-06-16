package script.db

databaseChangeLog(logicalFilePath: 'script/db/z_fix_iam.groovy') {

    changeSet(author: 'scp', id: '2021-09-26-sys-setting') {
        sql("UPDATE iam_sys_setting SET SETTING_VALUE = '#5365EA' WHERE SETTING_KEY = 'themeColor'")
    }

    changeSet(author: 'scp', id: '2021-10-09-iam-client-modify-column') {
        sql("ALTER TABLE oauth_client MODIFY COLUMN `web_server_redirect_uri` text BINARY comment '授权重定向URL'")
    }
}
