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

    changeSet(author: 'scp', id: '2021-04-16-modify-table') {
        sql("alter table iam_user modify language varchar(16);")
    }

    changeSet(author: 'scp', id: '2021-04-16-iam-member-role') {
        sql("UPDATE iam_member_role SET h_assign_level = 'organization' WHERE role_id = ( SELECT ir.id FROM iam_role ir WHERE ir.CODE = 'role/site/default/administrator' )")
    }

    changeSet(author: 'scp', id: '2021-05-08-iam-user') {
        sql("UPDATE iam_user SET id=0 where login_name='ANONYMOUS'")
    }

    changeSet(id: '2022-01-21-create-index', author: 'changping.shi@hand-china.com') {
        createIndex(tableName: 'IAM_MENU_PERMISSION', indexName: 'NK_IAM_MENU_PERMISSION_N2', unique: false) {
            column(name: 'PERMISSION_CODE')
        }
    }
    // 不要随意在这里加changeset 考虑全新初始化表不存在的情况 最好加在z_fix_iam
}
