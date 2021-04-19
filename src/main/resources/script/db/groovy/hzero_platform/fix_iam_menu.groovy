package script.db

databaseChangeLog(logicalFilePath: 'script/db/fix_iam_menu.groovy') {
    changeSet(author: 'scp', id: '2021-04-19-iam-menu-add-column') {
        addColumn(tableName: 'IAM_MENU') {
            column(name: 'MENU_VISIBILITY', type: "TINYINT UNSIGNED", remarks: '菜单可见度，标准版是1，高级版10，开发版30', afterColumn: 'fd_level')
        }
    }
}
