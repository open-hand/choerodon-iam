package io.choerodon.iam.infra.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.springframework.util.StringUtils;

/**
 * @author zmf
 * @since 05/22/20
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    /**
     * 保证在执行逻辑时不抛出异常的包装方法
     *
     * @param logger        调用方的；logger
     * @param actionInTry   正常处理的逻辑
     * @param actionInCatch 处理异常的逻辑
     */
    public static void doWithTryCatchAndLog(Logger logger, Runnable actionInTry, Consumer<Exception> actionInCatch) {
        if (actionInTry == null) {
            logger.info("Internal fault: parameter actionInTry is unexpectedly null. Action abort.");
            return;
        }
        if (actionInCatch == null) {
            logger.info("Internal fault: parameter actionInCatch is unexpectedly null. Action abort.");
            return;
        }

        try {
            actionInTry.run();
        } catch (Exception ex) {
            try {
                actionInCatch.accept(ex);
            } catch (Exception e) {
                logger.info("Exception occurred in actionInCatch.accept...");
            }
        }
    }


    /**
     * This is used to cut and drop the content that are longer than the specified max length.
     * For example,
     * you can use it to save message in db to ensure that the content won't overflow the database field capacity.
     *
     * @param content   content
     * @param maxLength max length to return
     * @return the result after cut
     */
    public static String cutOutString(String content, int maxLength) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        if (maxLength <= 0) {
            return content;
        }
        return content.length() > maxLength ? content.substring(0, maxLength) : content;
    }

    /**
     * read the content of the throwable to string
     *
     * @param throwable the throwable to be read
     * @return the content
     */
    public static String readContentOfThrowable(Throwable throwable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        throwable.printStackTrace(ps);
        ps.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }
}
