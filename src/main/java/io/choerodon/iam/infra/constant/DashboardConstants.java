package io.choerodon.iam.infra.constant;

public class DashboardConstants {

    /**
     * 业务异常
     */
    public static class ErrorCode {
        private ErrorCode() {}

        public static final String ERROR_DASHBOARD_NOT_EXIST = "error.dashboard.not.exist";
        public static final String ERROR_NOT_ASSIGN_DASHBOARD = "error.not.assign.dashboard";
        public static final String ERROR_ALREADY_ASSIGN_DASHBOARD = "error.already.assign.dashboard";
        public static final String ERROR_USER_GET = "error.user.get";
        public static final String ERROR_DASHBOARD_NOT_ASSIGN = "error.dashboard.not.assign";
    }
}
