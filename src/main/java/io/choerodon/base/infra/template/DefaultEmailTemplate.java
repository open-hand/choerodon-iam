package io.choerodon.base.infra.template;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.EmailTemplate;

/**
 * @author superlee
 * @since 0.16.0
 */
public interface DefaultEmailTemplate extends EmailTemplate {

    /**
     * 读取指定路径文件
     *
     * @param path
     * @throws IOException
     * @return
     */
    default String content(String path) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        String html;
        try {
            html = IOUtils.toString(inputStream, Charsets.UTF_8);
        } catch (IOException e) {
            throw new CommonException(e);
        } finally {
            inputStream.close();
        }
        return html;
    }

}
