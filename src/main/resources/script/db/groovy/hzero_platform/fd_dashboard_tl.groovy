package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_dashboard_tl.groovy') {
    changeSet(author: "changping.shi@hand-china.com", id: "2021-12-03-fd_dashboard_tl") {
        createTable(tableName: "fd_dashboard_tl", remarks: "") {
            column(name: "dashboard_id", type: "BIGINT(20)",  remarks: "")  {constraints(nullable:"false")}
            column(name: "dashboard_name", type: "varchar(128)",  remarks: "")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(24)",  remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"dashboard_id,lang",tableName:"fd_dashboard_tl",constraintName: "fd_dashboard_tl_u1")
    }
}