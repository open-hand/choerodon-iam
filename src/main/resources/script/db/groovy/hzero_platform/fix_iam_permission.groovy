package script.db

databaseChangeLog(logicalFilePath: 'script/db/fix_iam_permission.groovy') {
    changeSet(author: 'scp', id: '2020-11-24-iam-permission-modify-column') {
        sql("ALTER TABLE iam_permission MODIFY COLUMN `path` VARCHAR(256) BINARY comment '权限对应的api路径'")
    }

    changeSet(author: 'scp', id: '2020-12-17-iam-user') {
        sql("UPDATE iam_user SET phone = CONCAT(phone, '*#*') WHERE is_enabled = 0")
    }
    changeSet(author: 'scp', id: '2020-12-31-project-category') {
        sql("UPDATE fd_project_category fpc SET fpc.LABEL_CODE = concat( 'O_', fpc.LABEL_CODE ) WHERE fpc.`CODE` NOT LIKE 'N_%';")
    }
}
