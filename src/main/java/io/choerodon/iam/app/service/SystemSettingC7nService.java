package io.choerodon.iam.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.infra.dto.SysSettingDTO;

/**
 * 对系统设置进行增删改查
 *
 * @author zmf
 * @since 2018-10-15
 */
public interface SystemSettingC7nService {
    /**
     * 上传平台徽标(支持裁剪，旋转，并保存)
     *
     * @param file 徽标图片
     * @return 图片的地址
     */
    String uploadFavicon(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

    /**
     * 上传平台导航栏图形标(支持裁剪，旋转，并保存)
     *
     * @param file 图片
     * @return 图片的地址
     */
    String uploadSystemLogo(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

    /**
     * 添加/更新平台基本信息
     *
     * @param sysSettingVO 平台基本信息数据
     * @return 返回添加/更新后的平台基本信息
     */
    SysSettingVO updateGeneralInfo(SysSettingVO sysSettingVO);

    /**
     * 添加/更新平台密码策略
     *
     * @param sysSettingVO 平台密码策略数据
     * @return 返回添加/更新后的平台密码策略
     */
    SysSettingVO updatePasswordPolicy(SysSettingVO sysSettingVO);

    /**
     * 重置平台基本信息
     */
    void resetGeneralInfo();

    /**
     * 获取平台基本信息及密码策略
     *
     * @return ，如果存在返回数据，否则返回 null
     */
    SysSettingVO getSetting();

    /**
     * 获取是否启用项目/组织类型控制
     *
     * @return 是否启用项目/组织类型控制
     */
    Boolean getEnabledStateOfTheCategory();
}
