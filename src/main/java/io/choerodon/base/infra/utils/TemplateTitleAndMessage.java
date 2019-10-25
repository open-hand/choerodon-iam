package io.choerodon.base.infra.utils;

/**
 * @author jiameng.cao
 * @date 2019/9/19
 */
public final class TemplateTitleAndMessage {
    private TemplateTitleAndMessage() {
    }

    public static class Title {
        private Title() {
        }

        public static final String APP_PUBLISH_FAIL_TITLE = "应用发布失败";
        public static final String APP_SVC_UPDATE_FAIL_TITLE = "已发布版本更新失败";

        public static final String APP_VERSION_DOWNLOAD_PROCESSING_TITLE = "应用版本正在下载";
        public static final String APP_VERSION_DOWNLOAD_FAILED_TITLE = "应用版本下载失败";
        public static final String APP_VERSION_DOWNLOAD_COMPLETED_TITLE = "应用版本下载成功";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_TITLE = "正在下载更新版本";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_TITLE = "下载更新版本失败";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_TITLE = "下载更新版本成功";
    }

    public static class Message {
        private Message() {
        }

        public static final String APP_PUBLISH_FAIL_MSG = "<strong>${operator}</strong>,您好!您提交的&nbsp;<strong>${applicationName}&nbsp;${version}</strong>&nbsp;发布失败，请重试。";
        public static final String APP_SVC_UPDATE_FAIL_MSG = "<strong>${operator}</strong>,您好!您提交的&nbsp;<strong>${applicationName}&nbsp;${version}</strong>&nbsp;添加修复版本失败，请重试。";

        public static final String APP_VERSION_DOWNLOAD_PROCESSING_MSG = "<strong>${operator}</strong>,您好! 您下载的${applicationName}${version}已开始下载，请等待。";
        public static final String APP_VERSION_DOWNLOAD_FAILED_MSG = "<strong>${operator}</strong>,您好! 您下载&nbsp;<strong>${applicationName}&nbsp;${version}</strong>&nbsp;失败，请重新下载。";
        public static final String APP_VERSION_DOWNLOAD_COMPLETED_MSG = "<strong>${operator}</strong>,您好! 您下载&nbsp;<strong>${applicationName}&nbsp;${version}</strong>&nbsp;成功。";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_PROCESSING_MSG = "<strong>${operator}</strong>,您好! 您正在下载&nbsp;<strong>${applicationName}&nbsp;${version}</strong>已更新的修复版本，请稍等。";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_FAILED_MSG = "<strong>${operator}</strong>,您好! 您下载&nbsp;<strong>${applicationName}&nbsp;${version}&nbsp;</strong>已更新的修复版本失败。";
        public static final String APP_VERSION_DOWNLOAD_FIX_VERSION_COMPLETED_MSG = "<strong>${operator}</strong>,您好! 您成功下载&nbsp;<strong>${applicationName}&nbsp;${version}</strong>&nbsp;已更新的修复版本。";
    }
}
