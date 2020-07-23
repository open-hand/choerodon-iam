package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ResetPasswordVO;
import io.choerodon.iam.api.vo.SysSettingVO;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.valitador.SysSettingValidator;
import io.choerodon.iam.app.service.SystemSettingC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author zmf
 * @since 2018-10-15
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_SYSTEM_SETTING)
@RestController
@RequestMapping(value = "/choerodon/v1/system/setting")
public class SystemSettingC7nController extends BaseController {
    private final SystemSettingC7nService systemSettingService;

    public SystemSettingC7nController(SystemSettingC7nService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @PostMapping
    @ApiOperation(value = "添加/更新平台基本信息")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<SysSettingVO> updateGeneralInfo(@RequestBody @Validated({SysSettingValidator.GeneralInfoGroup.class})
                                                                  SysSettingVO sysSettingVO, BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(systemSettingService.updateGeneralInfo(sysSettingVO), HttpStatus.OK);
    }

    @PostMapping(value = "/passwordPolicy")
    @ApiOperation(value = "添加/更新平台密码策略")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<SysSettingVO> updatePasswordPolicy(@RequestBody @Validated({SysSettingValidator.PasswordPolicyGroup.class})
                                                                     SysSettingVO sysSettingVO, BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(systemSettingService.updatePasswordPolicy(sysSettingVO), HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "重置平台基本信息")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity resetGeneralInfo() {
        systemSettingService.resetGeneralInfo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "获取平台基本信息、密码策略及Feedback策略")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    public ResponseEntity<Object> getSetting() {
        SysSettingVO sysSettingVO = systemSettingService.getSetting();
        Object result;
        result = sysSettingVO == null ? "{}" : sysSettingVO;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/upload/favicon")
    @ApiOperation(value = "上传平台徽标")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<String> uploadFavicon(@RequestPart MultipartFile file,
                                                @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
                                                @RequestParam(required = false) Double rotate,
                                                @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
                                                @RequestParam(required = false, name = "startX") Integer axisX,
                                                @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
                                                @RequestParam(required = false, name = "startY") Integer axisY,
                                                @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
                                                @RequestParam(required = false, name = "endX") Integer width,
                                                @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
                                                @RequestParam(required = false, name = "endY") Integer height) {
        return new ResponseEntity<>(systemSettingService.uploadFavicon(file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }

    @PostMapping(value = "/upload/logo")
    @ApiOperation(value = "上传平台logo")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<String> uploadLogo(@RequestPart MultipartFile file,
                                             @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
                                             @RequestParam(required = false) Double rotate,
                                             @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
                                             @RequestParam(required = false, name = "startX") Integer axisX,
                                             @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
                                             @RequestParam(required = false, name = "startY") Integer axisY,
                                             @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
                                             @RequestParam(required = false, name = "endX") Integer width,
                                             @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
                                             @RequestParam(required = false, name = "endY") Integer height) {
        return new ResponseEntity<>(systemSettingService.uploadSystemLogo(file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }

    @GetMapping(value = "/enable_resetPassword")
    @ApiOperation(value = "是否允许修改仓库密码")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    public ResponseEntity<ResetPasswordVO> enableResetPassword() {
        SysSettingVO sysSettingVO = systemSettingService.getSetting();
        boolean result = !ObjectUtils.isEmpty(sysSettingVO) && !ObjectUtils.isEmpty(sysSettingVO.getResetGitlabPasswordUrl());
        ResetPasswordVO resetPasswordVO = new ResetPasswordVO();
        if (result) {
            resetPasswordVO.setEnable_reset(true);
            resetPasswordVO.setResetGitlabPasswordUrl(sysSettingVO.getResetGitlabPasswordUrl());
        } else {
            resetPasswordVO.setEnable_reset(false);
        }
        return new ResponseEntity<>(resetPasswordVO, HttpStatus.OK);
    }

    @GetMapping(value = "/enable_category")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("是否开启项目/组织类型控制")
    public ResponseEntity<Boolean> getEnabledStateOfTheCategory() {
        return new ResponseEntity<>(systemSettingService.getEnabledStateOfTheCategory(), HttpStatus.OK);
    }
}
