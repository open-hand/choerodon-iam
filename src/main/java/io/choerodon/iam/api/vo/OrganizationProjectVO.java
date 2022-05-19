package io.choerodon.iam.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Collections;
import java.util.List;

public class OrganizationProjectVO {
    private List<Project> projectList = Collections.emptyList();

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public static Project newInstanceProject(Long id, String name, String code, String organizationName, Boolean dtEditEnable) {
        return new Project(id, name, code, organizationName, dtEditEnable);
    }

    public static class Project {
        private Long id;
        private String name;
        private String code;
        private String organizationName;
        private Boolean dtEditEnable;

        public Project(Long id, String name, String code, String organizationName, Boolean dtEditEnable) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.organizationName = organizationName;
            this.dtEditEnable = dtEditEnable;
        }

        //feign需要默认参数，以便反序列化
        public Project() {
        }

        public Boolean getDtEditEnable() {
            return dtEditEnable;
        }

        public void setDtEditEnable(Boolean dtEditEnable) {
            this.dtEditEnable = dtEditEnable;
        }

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

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
    }
}
