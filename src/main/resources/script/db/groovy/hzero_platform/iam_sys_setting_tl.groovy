package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_sys_setting_tl.groovy') {
    changeSet(id: '2022-07-14-iam_sys_setting_tl', author: 'wx') {
        createTable(tableName: "iam_sys_setting_tl", remarks: '平台设置多语言表') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '平台设置表的主键') {
                constraints(nullable: false)
            }

            column(name: 'SETTING_VALUE', type: 'VARCHAR(255)', remarks: '配置属性值多语言') {
            }


            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言') {
                constraints(nullable: false)
            }
        }
        addUniqueConstraint(tableName: 'iam_sys_setting_tl', constraintName: 'iam_sys_setting_tl_u1', columnNames: 'ID,LANG')
    }


}