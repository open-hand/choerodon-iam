package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_report.groovy') {
    changeSet(author: 'xausky@163.com', id: '2019-09-10-fd-report') {
        createTable(tableName: "FD_REPORT", remarks: "报表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_REPORT')
            }
            column(name: 'REPORT_TYPE', type: 'VARCHAR(64)', remarks: '类型')
            column(name: 'ICON', type: 'MEDIUMTEXT', remarks: '图标')
            column(name: 'TITLE', type: 'VARCHAR(64)', remarks: '标题')
            column(name: 'DESCRIPTION', type: 'VARCHAR(128)', remarks: '描述')
            column(name: 'PATH', type: 'VARCHAR(128)', remarks: '路径')
            column(name: 'SORT', type: 'BIGINT UNSIGNED', remarks: '图表类型里层排序')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(author: 'scp', id: '2021-01-13-fd_report_add_index') {
        createIndex(indexName: 'uk_TITLE', tableName: 'FD_REPORT', unique: true) {
            column(name: 'REPORT_TYPE')
            column(name: 'TITLE')
        }
    }
    changeSet(author: 'scp', id: '2021-01-15-fd-fd_report-add-column') {
        addColumn(tableName: 'fd_report') {
            column(name: 'type_sequence', type: "BIGINT UNSIGNED", remarks: '图表类型顺序', defaultValue: '10', afterColumn: 'SORT')
        }
    }
    changeSet(author: 'scp', id: '2021-04-23-fd_report-add-column') {
        addColumn(tableName: 'fd_report') {
            column(name: 'VISIBILITY', type: "TINYINT UNSIGNED", remarks: '图表可见度，标准版是1，高级版10，开发版30', afterColumn: 'type_sequence', defaultValue: "1")
        }
    }
    changeSet(author: 'wx', id: '2021-05-24-fd_report-add-column') {
        addColumn(tableName: 'fd_report') {
            column(name: 'MODULE', type: "VARCHAR(32)", remarks: '图表所属的菜单模块', afterColumn: 'type_sequence')
        }
    }

    changeSet(author: 'wanghao', id: '2021-12-06-fd_report-add-column') {
        dropIndex(tableName: "fd_report", indexName: "uk_TITLE")
        addColumn(tableName: 'fd_report') {
            column(name: 'code', type: "VARCHAR(32)", remarks: '图表编码', afterColumn: 'type_sequence')
        }
        addUniqueConstraint(tableName: 'fd_report', columnNames: 'REPORT_TYPE,code', constraintName: 'uk_type_code')
    }
    // 新增多语言，数据结构调整，需要重新初始化
    changeSet(author: 'wanghao', id: '2021-12-07-fd_report-drop-column') {
        sql("DELETE FROM fd_report")
    }
    changeSet(author: 'wanghao', id: '2021-12-07-updateDataType') {
        modifyDataType(tableName: 'fd_report', columnName: 'TITLE', newDataType: 'VARCHAR(512)')
        modifyDataType(tableName: 'fd_report', columnName: 'DESCRIPTION', newDataType: 'VARCHAR(2048)')
    }
}