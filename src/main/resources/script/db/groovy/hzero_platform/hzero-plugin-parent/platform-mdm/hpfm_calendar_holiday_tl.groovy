package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_calendar_holiday_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_calendar_holiday_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_calendar_holiday_tl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_calendar_holiday_tl", remarks: "日历假期多语言表") {
            column(name: "holiday_id", type: "bigint(20)",  remarks: "平台级日历公共假期表ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "holiday_name", type: "varchar(" + 30 * weight + ")",  remarks: "公共假期名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"holiday_id,lang",tableName:"hpfm_calendar_holiday_tl",constraintName: "hpfm_calendar_holiday_tl_u1")
    }
}