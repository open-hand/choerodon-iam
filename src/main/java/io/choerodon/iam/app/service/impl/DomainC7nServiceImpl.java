package io.choerodon.iam.app.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.base.app.service.DomainC7nService;

/**
 * @author superlee
 * @since 2019-06-19
 */
@Service
public class DomainC7nServiceImpl implements DomainC7nService {

    private static final String HTTP = "http://";

    private static final String HTTPS = "https://";

    @Override
    public boolean check(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        boolean hasProtocol = false;
        if (url.startsWith(HTTP)) {
            hasProtocol = true;
            url = url.substring(HTTP.length());
        } else if (url.startsWith(HTTPS)) {
            hasProtocol = true;
            url = url.substring(HTTPS.length());
        }
        if (!hasProtocol) {
            return false;
        }
        String[] urls = url.split("/");
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(urls[0]);
        } catch (UnknownHostException e) {
            //do nothing
        }
        return inetAddress != null;
    }
}
