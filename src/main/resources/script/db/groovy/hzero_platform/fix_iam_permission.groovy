package script.db

databaseChangeLog(logicalFilePath: 'script/db/fix_iam_permission.groovy') {
    changeSet(author: 'scp', id: '2020-11-24-iam-permission-modify-column') {
        sql("ALTER TABLE iam_permission MODIFY COLUMN `path` VARCHAR(256) BINARY comment '权限对应的api路径'")
    }
}
