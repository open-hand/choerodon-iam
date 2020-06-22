package io.choerodon.iam.infra.enums;

import java.util.Random;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/22 16:44
 */
public enum ProjectHeadColorEnum {
    COLOR_A("linear-gradient(225deg,rgba(152,229,218,1) 0%,rgba(0,191,165,1) 100%)"),
    COLOR_B("linear-gradient(226deg,rgba(255,212,163,1) 0%,rgba(255,185,106,1) 100%)"),
    COLOR_C("linear-gradient(226deg,rgba(161,188,245,1) 0%,rgba(104,135,232,1) 100%)"),
    COLOR_D("linear-gradient(226deg,rgba(255,177,185,1) 0%,rgba(244,133,144,1) 100%)");
    private String value;

    public String value() {
        return value;
    }


    ProjectHeadColorEnum(String value) {
        this.value = value;
    }

    public static String getByRandom() {
        int length = ProjectHeadColorEnum.values().length;
        int i = new Random().nextInt(length);
        return ProjectHeadColorEnum.values()[i].value();
    }
}
