package io.choerodon.base.app.service;

/**
 * @author superlee
 * @since 2019-06-19
 */
public interface DomainService {
    /**
     * 检查url是否存在，可以访问
     * @param url
     * @return
     */
    boolean check(String url);
}
