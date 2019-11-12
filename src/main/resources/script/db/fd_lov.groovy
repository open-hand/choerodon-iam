package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_lov.groovy') {

    changeSet(author: "hailor", id: "2019-09-06-add-table-fd_lov") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_LOV_S', startValue: "10001")
        }
        createTable(tableName: "FD_LOV",remarks: 'LOV定义') {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LOV')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '编码') {
                constraints(nullable: false)
            }
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "RESOURCE_LEVEL", type: "varchar(240)", remarks: "LOV所在级别，SIT,ORGANIZATION,PROJECT")
            column(name: "PERMISSION_CODE", type: "varchar(225)", remarks: "LOV取数API对应的permission code")
            column(name: "VALUE_FIELD", type: "varchar(80)", remarks: "值字段")
            column(name: "TEXT_FIELD", type: "varchar(80)", remarks: "现实字段")
            column(name: "TITLE", type: "varchar(225)", remarks: "标题")
            column(name: "WIDTH", type: "decimal(20,0)", remarks: "LOV弹窗宽度")
            column(name: "HEIGHT", type: "decimal(20,0)", remarks: "LOV弹窗高度")
            column(name: "PLACEHOLDER", type: "varchar(80)", remarks: "提示")
            column(name: "DELAY_LOAD_FLAG", type: "TINYINT UNSIGNED", defaultValue: "0", remarks: "是否延迟加载")
            column(name: "EDIT_FLAG", type: "TINYINT UNSIGNED", defaultValue: "0", remarks: "是否可编辑")
            column(name: "TREE_FLAG", type: "TINYINT UNSIGNED", defaultValue: "1", remarks: "是否树形")
            column(name: "ID_FIELD", type: "varchar(80)", defaultValue: "1", remarks: "树形的id字段")
            column(name: "PARENT_FIELD", type: "varchar(80)", defaultValue: "1", remarks: "树形的父亲字段")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'FD_LOV', columnNames: 'CODE', constraintName: 'UK_FD_LOV_U1')
    }

    changeSet(author: "hailor", id: "2019-09-06-add-table-base_lov_field") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_LOV_FIELD_S', startValue: "10001")
        }
        createTable(tableName: "FD_LOV_GRID_FIELD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LOV_GRID_FIELD')
            }

            column(name: "LOV_CODE", type: "varchar(255)", remarks: "LOV编码")
            column(name: "GRID_FIELD_DISPLAY_FLAG", type: "TINYINT UNSIGNED", defaultValue: "1", remarks: "是否显示表格列")
            column(name: "GRID_FIELD_LABEL", type: "varchar(255)", remarks: "表格列描述")
            column(name: "GRID_FIELD_NAME", type: "varchar(80)", remarks: "表格列字段名")
            column(name: "GRID_FIELD_ORDER", type: "decimal(20,0)", defaultValue: "1", remarks: "表格列排序号")
            column(name: "GRID_FIELD_ALIGN", type: "varchar(10)", defaultValue: "center", remarks: "表格列布局")
            column(name: "GRID_FIELD_WIDTH", type: "decimal(20,0)", remarks: "表格列宽")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "hailor", id: "2019-09-06-add-table-fd_lov_query_field") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FD_LOV_QUERY_FIELD_S', startValue: "10001")
        }
        createTable(tableName: "FD_LOV_QUERY_FIELD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_FD_LOV_GRID_FIELD')
            }
            column(name: "LOV_CODE", type: "varchar(255)", remarks: "LOV编码")
            column(name: "QUERY_FIELD_DISPLAY_FLAG", type: "TINYINT UNSIGNED", defaultValue: "1", remarks: "是否显示此查询字段")
            column(name: "QUERY_FIELD_REQUIRED_FLAG", type: "TINYINT UNSIGNED", defaultValue: "0", remarks: "是否必须存在")
            column(name: "QUERY_FIELD_LABEL", type: "varchar(255)", remarks: "表格列描述")
            column(name: "QUERY_FIELD_NAME", type: "varchar(80)", remarks: "表格列字段名")
            column(name: "QUERY_FIELD_WIDTH", type: "decimal(20,0)", remarks: "查询字段宽度")
            column(name: "QUERY_FIELD_TYPE", type: "varchar(30)", remarks: "查询字段组件类型") // HiddenString, HiddenNumber
            column(name: "QUERY_FIELD_LOOKUP_TYPE", type: "varchar(80)", remarks: "查询字段combobox对应的快码值")
            column(name: "QUERY_FIELD_LOV_CODE", type: "varchar(80)", remarks: "查询字段lov对应的通用lov编码")
            column(name: "QUERY_FIELD_ORDER", type: "decimal(20,0)", remarks: "查询字段排序号")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }

    changeSet(author: 'xausky', id: '2019-09-17-add-query-field-default-value'){
        addColumn(tableName: 'FD_LOV_QUERY_FIELD'){
            column(name: "QUERY_FIELD_PARAM_TYPE", type: "varchar(16)", remarks: "查询字段参数类型") //Query, PATH
            column(name: "QUERY_FIELD_DEFAULT", type: "varchar(255)", remarks: "查询字段默认值")
        }
    }

    changeSet(author: 'bgzyy', id: '2019-10-31-lov-add-column') {
        addColumn(tableName: 'FD_LOV') {
            column(name: "PAGE_FLAG", type: "TINYINT UNSIGNED", defaultValue: "1", remarks: "是否分页")
            column(name: "PAGE_SIZE", type: "INT UNSIGNED", defaultValue: "10", remarks: "每页默认记录数")
            column(name: "MULTIPLE_FLAG", type: "TINYINT UNSIGNED", defaultValue: "0", remarks: "是否多选")
        }
    }

    changeSet(author: 'bgzyy', id: '2019-10-31-lov-grid-add-column') {
        addColumn(tableName: 'FD_LOV_GRID_FIELD') {
            column(name: "GRID_FIELD_QUERY_FLAG", type: "TINYINT UNSIGNED", defaultValue: "0", remarks: "是否为查询字段")
        }
    }

    changeSet(author: 'bgzyy', id: '2019-10-31-lov-query-rename-column') {
        renameColumn(tableName: 'FD_LOV_QUERY_FIELD', oldColumnName: 'QUERY_FIELD_LOOKUP_TYPE', newColumnName: 'QUERY_FIELD_LOOKUP_CODE', columnDataType: 'varchar(80)')
    }
}