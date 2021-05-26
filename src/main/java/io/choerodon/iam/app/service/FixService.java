package io.choerodon.iam.app.service;

public interface FixService {

    void fixProjectCateGory();

    void fixRealNameToPinyin();

    void fixRealNameToPinyinHeaderChar();

    /**
     * 修复菜单层级
     */
    void fixMenuLevelPath(Boolean initAll);

}
