package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_report_tl.groovy') {
    changeSet(author: "wanghao", id: "2021-12-06-fd_report_tl") {
        createTable(tableName: "fd_report_tl", remarks: "") {
            column(name: "id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(16)",  remarks: "")  {constraints(nullable:"false")}
            column(name: 'TITLE', type: "varchar(128)", remarks: '标题')
            column(name: 'DESCRIPTION', type: "varchar(128)", remarks: '描述')
        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"fd_report_tl",constraintName: "fd_report_tl_u1")
    }
}