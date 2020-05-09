package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_config_field.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_config_field") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_config_field_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_config_field") {
            column(name: "config_field_id", type: "bigint(20)", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint(20)", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "unit_id", type: "bigint(20)", remarks: "个性化单元ID") {
                constraints(nullable: false)
            }
            column(name: "model_id", type: "bigint(20)", remarks: "模型ID") {
                constraints(nullable: false)
            }
            column(name: "field_id", type: "bigint(20)", remarks: "字段ID") {
                constraints(nullable: false)
            }
            column(name: "field_name", type: "varchar(" + 255 * weight + ")", remarks: "字段名称")
            column(name: "field_editable", type: "smallint(1)", defaultValue: "1", remarks: "是否可编辑") {
                constraints(nullable: false)
            }
            column(name: "field_required", type: "smallint(1)", defaultValue: "0", remarks: "是否必输") {
                constraints(nullable: false)
            }
            column(name: "grid_seq", type: "Int(5)", remarks: "字段排序号")
            column(name: "field_alias", type: "varchar(" + 255 * weight + ")", remarks: "字段别名")
            column(name: "field_visible", type: "smallint(1)", defaultValue: "1", remarks: "是否显示字段")
            column(name: "form_col", type: "smallint(3)", remarks: "表单列序号")
            column(name: "form_row", type: "smallint(3)", remarks: "表单行序号")
            column(name: "grid_fixed", type: "varchar(" + 30 * weight + ")", remarks: "表格冻结配置")
            column(name: "grid_width", type: "smallint(3)", remarks: "表格列宽度")
            column(name: "render_options", type: "varchar(" + 30 * weight + ")", defaultValue: "WIDGET'", remarks: "渲染类型") {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "bigint(20)", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "unit_id,field_id,tenant_id", tableName: "hpfm_cusz_config_field", constraintName: "hpfm_cusz_config_field_U1")

        createTable(tableName: "hpfm_cusz_config_field_tl") {
            column(name: "config_field_id", type: "bigint(20)", remarks: "个性化配置ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "lang", type: "varchar(8)", remarks: "租户ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "field_name", type: "varchar(255)", remarks: "多语言字段")
        }
    }
    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-08_hpfm_cusz_config_field") {
        addColumn(tableName: 'hpfm_cusz_config_field') {
            column(name: "label_col", type: "smallint(3)", remarks: "label列数")
        }
        addColumn(tableName: 'hpfm_cusz_config_field') {
            column(name: "wrapper_col", type: "smallint(3)", remarks: "wrapper列数")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-08_hpfm_cusz_config_field_index") {
        dropUniqueConstraint(tableName: 'hpfm_cusz_config_field', constraintName: 'hpfm_cusz_config_field_U1')
        createIndex(tableName: "hpfm_cusz_config_field", indexName: "hpfm_cusz_config_field_index1") {
            column(name: "unit_id")
            column(name: "field_id")
            column(name: "tenant_id")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-11_hpfm_cusz_config_field_add_user_id") {
        addColumn(tableName: 'hpfm_cusz_config_field') {
            column(name: "user_id", type: "bigint(20)", remarks: "用户id", defaultValue: "-1")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-03-04_hpfm_cusz_config_field_add_u1_code") {
        addColumn(tableName: 'hpfm_cusz_config_field') {
            column(name: "field_code", type: "varchar(" + 255 * weight + ")", remarks: "字段编码")
        }
        addUniqueConstraint(columnNames: "unit_id, user_id ,field_id,field_code,tenant_id", tableName: "hpfm_cusz_config_field", constraintName: "hpfm_cusz_config_field_U1")
    }
}
