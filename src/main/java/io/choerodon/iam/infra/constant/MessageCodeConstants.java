package io.choerodon.iam.infra.constant;

/**
 * @author scp
 * 消息类型code
 * @date 2020/5/14
 * @description
 */
public class MessageCodeConstants {


    public static final String ADD_MEMBER = "ADDMEMBER";
    public static final String SITE_ADD_USER = "SITEADDUSER";
    public static final String PROJECT_ADD_USER = "PROJECTADDUSER";
    public static final String STOP_USER = "STOPUSER";
    public static final String RESET_ORGANIZATION_USER_PASSWORD = "RESETORGANIZATIONUSERPASSWORD";

    //组织层
    //启用组织
    public static final String ENABLEORGANIZATION = "ENABLEORGANIZATION";
    //组织任务状态通知
    public static final String JOBSTATUSORGANIZATION = "JOBSTATUSORGANIZATION";
    //管理员导入添加用户
    public static final String ADDUSER = "ADDUSER";
    public static final String DISABLE_ORGANIZATION = "DISABLEORGANIZATION";
    public static final String INVITE_USER = "INVITEUSER";

    //项目层
    //停用项目
    public static final String DISABLEPROJECT = "DISABLEPROJECT";
    //启用项目
    public static final String ENABLEPROJECT = "ENABLEPROJECT";


}
