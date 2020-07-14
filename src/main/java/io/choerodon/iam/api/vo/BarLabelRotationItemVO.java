package io.choerodon.iam.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/2/26 10:17
 */
public class BarLabelRotationItemVO {
    @Encrypt
    private Long id;
    private String name;
    private List<Long> data = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getData() {
        return data;
    }

    public void setData(List<Long> data) {
        this.data = data;
    }
}
