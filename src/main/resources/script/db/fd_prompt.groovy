package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_lov_lookupcode_prompt.groovy') {

    changeSet(author: "hailor", id: "2019-09-06-add-table-fd_prompt") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_PROMPT_S', startValue: "10001")
        }
        createTable(tableName: "FD_PROMPT") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LOV_GRID_FIELD')
            }
            column(name: "PROMPT_CODE", type: "varchar(255)", remarks: "文本编码")
            column(name: "LANG", type: "varchar(10)", remarks: "语言")
            column(name: "SERVICE_CODE", type: "varchar(255)", remarks: "服务编码")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_PROMPT', columnNames: "PROMPT_CODE,LANG", constraintName: 'UK_FD_PROMPT_U1')
    }



}