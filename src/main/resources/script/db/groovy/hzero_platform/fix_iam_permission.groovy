package script.db

databaseChangeLog(logicalFilePath: 'script/db/fix_iam_permission.groovy') {
    changeSet(author: 'scp', id: '2020-11-24-iam-permission-modify-column') {
        sql("ALTER TABLE iam_permission MODIFY COLUMN `path` VARCHAR(256) BINARY comment '权限对应的api路径'")
    }

    changeSet(author: 'scp', id: '2020-12-17-iam-user') {
        sql("UPDATE iam_user SET phone = CONCAT(phone, '*#*') WHERE is_enabled = 0")
    }

    changeSet(author: 'scp', id: '2021-01-29-delete-label') {
        sql("DELETE FROM iam_label WHERE NAME = 'SITE_MGR' OR NAME = 'TENANT_MGR'")
    }

    changeSet(author: 'scp', id: '2021-03-26-modify-table') {
        sql("alter table iam_user modify language varchar(16) null;")
    }
}
