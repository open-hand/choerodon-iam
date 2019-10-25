#!/bin/bash

: ${base_service:=iam_service}
: ${agile_service:=agile_service}

agile_service_table_atzwc="agile_time_zone_work_calendar"
agile_service_table_atzwcr="agile_time_zone_work_calendar_ref"
agile_service_table_awchr="agile_work_calendar_holiday_ref"
base_service_table_time_zone_calendar="iam_time_zone_work_calendar"
base_service_table_time_zone_calendar_ref="iam_time_zone_work_calendar_ref"
base_service_table_calendar_holiday_ref="iam_work_calendar_holiday_ref"
base_service_table_system_setting="iam_system_setting"
base_service_table_sys_setting="iam_sys_setting"
base_service_table_iam_role_permission="iam_role_permission"
base_service_table_iam_permission="iam_permission"
base_service_table_iam_menu_permission="iam_menu_permission"
base_service_table_fd_category_menu="fd_category_menu"
base_service_table_iam_menu_tl="iam_menu_tl"
base_service_table_iam_menu_b="iam_menu_b"
base_service_table_fd_project="fd_project"
base_service_table_fd_project_category="fd_project_category"
base_service_table_fd_project_map_category="fd_project_map_category"
base_service_table_iam_role_label="iam_role_label"
base_service_table_iam_label="iam_label"
base_service_table_iam_role_tl="iam_role_tl"
base_service_table_iam_role="iam_role"

: ${AgileDBHOST:=127.0.0.1}
: ${AgileDBUSER:=root}
: ${AgileDBPASS:=handhand}
: ${AgileDBPORT:=3306}

: ${BaseDBHOST:=127.0.0.1}
: ${BaseDBUSER:=root}
: ${BaseDBPASS:=handhand}
: ${BaseDBPORT:=3306}

# 各个微服务的数据在不同服务器上

#同步agile_time_zone_work_calendar数据到base_service
# 导出旧表数据及结构到需要被迁移的数据库表中
mysqldump -u$AgileDBUSER -p$AgileDBPASS -h $AgileDBHOST -P$AgileDBPORT \
${agile_service} ${agile_service_table_atzwc} \
| mysql -u$BaseDBUSER -p$BaseDBPASS -h $BaseDBHOST -P$BaseDBPORT ${base_service}

#同步agile_time_zone_work_calendar_ref数据到base_service
# 导出旧表数据及结构到需要被迁移的数据库表中
mysqldump -u$AgileDBUSER -p$AgileDBPASS -h $AgileDBHOST -P$AgileDBPORT \
${agile_service} ${agile_service_table_atzwcr} \
| mysql -u$BaseDBUSER -p$BaseDBPASS -h $BaseDBHOST -P$BaseDBPORT ${base_service}

#同步agile_work_calendar_holiday_ref数据到base_service
# 导出旧表数据及结构到需要被迁移的数据库表中
mysqldump -u$AgileDBUSER -p$AgileDBPASS -h $AgileDBHOST -P$AgileDBPORT \
${agile_service} ${agile_service_table_awchr} \
| mysql -u$BaseDBUSER -p$BaseDBPASS -h $BaseDBHOST -P$BaseDBPORT ${base_service}

echo '开始同步数据'
# 迁移数据到新表并删除旧表
mysql -u$BaseDBUSER -p$BaseDBPASS -h $BaseDBHOST -P$BaseDBPORT << EOF
use ${base_service};
drop procedure if exists sync_data;
delimiter //
create procedure sync_data()
begin
declare sysSettingNum int;
declare systemSettingNum int;
declare oldMenuNum int;
declare syncTimeZoneCNum int;
declare syncTimeZoneCRefNum int;
declare syncCHolidayRefNum int;

select count(time_zone_id) into syncTimeZoneCNum from ${base_service_table_time_zone_calendar};
if (syncTimeZoneCNum = 0) then
insert into ${base_service_table_time_zone_calendar}(time_zone_id, area_code, time_zone_code, organization_id, use_holiday, saturday_work, sunday_work) select time_zone_id, area_code, time_zone_code, organization_id, use_holiday, saturday_work, sunday_work from ${agile_service_table_atzwc};
end if;

select count(calendar_id) into syncTimeZoneCRefNum from ${base_service_table_time_zone_calendar_ref};
if (syncTimeZoneCRefNum = 0) then
insert into ${base_service_table_time_zone_calendar_ref}(calendar_id, time_zone_id, work_day, year, organization_id, status) select calendar_id, time_zone_id, work_day, year, organization_id, status from ${agile_service_table_atzwcr};
end if;

select count(calendar_id) into syncCHolidayRefNum from ${base_service_table_calendar_holiday_ref};
if (syncCHolidayRefNum > 0) then
truncate table ${base_service_table_calendar_holiday_ref};
insert into ${base_service_table_calendar_holiday_ref}(calendar_id, name, holiday, status, year) select calendar_id, name, holiday, status, year from ${agile_service_table_awchr};
end if;

# 同步系统配置数据
select count(id) into sysSettingNum from ${base_service_table_sys_setting};
select count(id) into systemSettingNum from ${base_service_table_system_setting};
if(systemSettingNum > 0 and sysSettingNum <= 0) then
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select  'favicon' as setting_key, favicon as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'systemLogo', system_logo as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'systemTitle' as setting_key, system_title as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'systemName' as setting_key, system_name as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'registerEnabled' as setting_key, register_enabled as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'registerUrl' as setting_key, register_url as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'resetGitlabPasswordUrl' as setting_key, reset_gitlab_password_url as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'defaultLanguage' as settingsetting_keyKey, default_language as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'defaultPassword' as setting_key, default_password as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'minPasswordLength' as setting_key, min_password_length as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value)
select 'maxPasswordLength'  as setting_key, max_password_length as setting_value from ${base_service_table_system_setting} limit 1;
insert into ${base_service_table_sys_setting}(setting_key, setting_value) values('themeColor',null);
end if;

select count(id) into oldMenuNum from ${base_service_table_iam_menu_b} where service_code='iam-service';
if(oldMenuNum > 0) then
# 删除角色权限
delete from ${base_service_table_iam_role_permission}
where permission_code in (select code from ${base_service_table_iam_permission}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki')
);
# 删除权限
delete from ${base_service_table_iam_permission}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki');
# 删除菜单权限
delete from ${base_service_table_iam_menu_permission}
where menu_code in (select code from ${base_service_table_iam_menu_b}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki')
);
# 删除菜单分类
delete from ${base_service_table_fd_category_menu}
where menu_code in (select code from ${base_service_table_iam_menu_b}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki')
);
# 删除菜单多语言
delete  from ${base_service_table_iam_menu_tl}
where id in (select id from ${base_service_table_iam_menu_b}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki')
);
# 删除菜单
delete from ${base_service_table_iam_menu_b}
where service_code in ('organization-service','iam-service','sms-service','notify-service','asgard-service','manager-service','devops-service','program-service','knowledgebase-service',
'test-manager-service','issue-service','agile-service','state-machine-service' ,'foundation-service','wiki-service','apim-service','devops','issue','knowledge','testManager','wiki');
end if;

# 修复项目类型关系
update ${base_service_table_fd_project_map_category}
set category_id = (select id as category_id from ${base_service_table_fd_project_category} where code='GENERAL')
where category_id = (select id as category_id from ${base_service_table_fd_project_category} where code='AGILE');
# 修复项目本身类型
update ${base_service_table_fd_project} set category='GENERAL' where category='AGILE';

# 删除部署管理员、项目管理员、wiki角色标签中间关系
delete
from ${base_service_table_iam_role_label}
where role_id in (select id as role_id from ${base_service_table_iam_role} where code in ('role/project/default/deploy-administrator','role/project/default/administrator'))
or label_id in (select id as label_id from ${base_service_table_iam_label} where name in ('organization.wiki.admin','organization.wiki.user','project.wiki.admin','project.wiki.user'));
# 删除wiki角色标签
delete
from ${base_service_table_iam_label}
where name in ('organization.wiki.admin','organization.wiki.user','project.wiki.admin','project.wiki.user');
# 删除部署管理员、项目管理员多语言
delete
from ${base_service_table_iam_role_tl}
where id in (select id from  ${base_service_table_iam_role} where code in ('role/project/default/deploy-administrator','role/project/default/administrator'));
# 删除部署管理员、项目管理员
delete
from ${base_service_table_iam_role}
where code in ('role/project/default/deploy-administrator','role/project/default/administrator');

end//
delimiter ;

SET @@autocommit=0;
call sync_data;
SET @@autocommit=1;
drop table ${agile_service_table_atzwc};
drop table ${agile_service_table_atzwcr};
drop table ${agile_service_table_awchr};
drop procedure if exists sync_data;
EOF
echo '同步数据结束'