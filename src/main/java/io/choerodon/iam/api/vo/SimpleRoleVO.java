package io.choerodon.iam.api.vo;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/7/21
 * @Modified By:
 */
public class SimpleRoleVO {
    private Long id;
    private String name;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
