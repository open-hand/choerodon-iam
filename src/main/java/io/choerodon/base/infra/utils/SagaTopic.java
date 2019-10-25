package io.choerodon.base.infra.utils;

public final class SagaTopic {

    private SagaTopic() {
    }

    public static class User {
        private User() {
        }

        /**
         * 创建用户
         */
        public static final String USER_CREATE = "iam-create-user";
        /**
         * iam接收创建组织事件的SagaTaskCode
         */
        public static final String TASK_USER_CREATE = "task-create-user";
        /**
         * 批量创建用户
         */
        public static final String USER_CREATE_BATCH = "iam-create-user";
        /**
         * 更新用户
         */
        public static final String USER_UPDATE = "iam-update-user";
        /**
         * 删除用户
         */
        public static final String USER_DELETE = "iam-delete-user";
        /**
         * 启用用户
         */
        public static final String USER_ENABLE = "iam-enable-user";
        /**
         * 停用用户
         */
        public static final String USER_DISABLE = "iam-disable-user";
    }

    public static class Project {
        private Project() {
        }

        //创建项目
        public static final String PROJECT_CREATE = "iam-create-project";
        //更新项目
        public static final String PROJECT_UPDATE = "iam-update-project";
        //停用项目
        public static final String PROJECT_DISABLE = "iam-disable-project";
        //启用项目
        public static final String PROJECT_ENABLE = "iam-enable-project";

    }

    public static class MemberRole {
        private MemberRole() {
        }

        //更新用户角色
        public static final String MEMBER_ROLE_UPDATE = "iam-update-memberRole";

        //删除用户角色
        public static final String MEMBER_ROLE_DELETE = "iam-delete-memberRole";
    }

    public static class Organization {
        private Organization() {
        }

        //启用组织
        public static final String ORG_ENABLE = "iam-enable-organization";

        //停用组织
        public static final String ORG_DISABLE = "iam-disable-organization";

        //更新组织
        public static final String ORG_UPDATE = "iam-update-organization";

    }

    public static class SystemSetting {
        private SystemSetting() {
        }

        // base 系统设置发生改变时（增加，更新，重置），触发 Saga 流程时的 code
        public static final String SYSTEM_SETTING_UPDATE = "iam-update-system-setting";
    }

    public static class Application {
        private Application() {
        }

        public static final String APPLICATION_DOWNLOAD = "base-download-application";

        // base 应用版本及修复版本下载事件
        public static final String APPLICATION_DOWNLOAD_COMPLETED = "base-download-application-version-completed";
        public static final String APPLICATION_DOWNLOAD_FAILED = "base-download-application-version-failed";
        public static final String APPLICATION_DOWNLOAD_PROCESSING = "base-download-application-version-processing";

        public static final String TASK_APPLICATION_DOWNLOAD_COMPLETED = "task-base-download-application-version-completed";
        public static final String TASK_APPLICATION_DOWNLOAD_FAILED = "task-base-download-application-version-failed";
        public static final String TASK_APPLICATION_DOWNLOAD_PROCESSING = "task-base-download-application-version-processing";
    }

    public static class ProjectRelationship {
        private ProjectRelationship() {
        }

        // iam新增项目关系
        public static final String PROJECT_RELATIONSHIP_ADD = "iam-add-project-relationships";
        // iam删除项目关系
        public static final String PROJECT_RELATIONSHIP_DELETE = "iam-delete-project-relationships";
    }


    public static class RemoteToken {
        private RemoteToken() {
        }

        // 组织更新远程连接Token
        public static final String REMOTE_TOKEN_ADD = "base-create-org-remote-token";
        // 组织失效远程连接Token
        public static final String REMOTE_TOKEN_EXPIRED = "base-expired-org-remote-token";
        // 组织生效远程连接Token
        public static final String REMOTE_TOKEN_RENEWAL = "base-renewal-org-remote-token";

    }


    public static class PublishApp {
        private PublishApp() {
        }

        // 发布应用事务
        public static final String PUBLISH_APP = "base-publish-market-app";

        //应用发布失败
        public static final String PUBLISH_APP_FAIL = "base-publish-market-app-fail";
        public static final String TASK_PUBLISH_APP_FAIL = "task-base-publish-market-app-fail";

        // 发布修复版本
        public static final String PUBLISH_APP_FIX_VERSION = "base-publish-market-app-fix-version";

    }
}
