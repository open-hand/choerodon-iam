package io.choerodon.iam.api.controller.compatible;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.domain.Page;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 这个类是用于放置一些对于外部已经安装的组件(例如集群监控中的prometheus, sonarqube)保证向后兼容的接口
 *
 * @author zmf
 * @since 2020/6/1
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_COMPATIBLE)
@RestController
public class CompatibleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompatibleController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserC7nService userC7nService;

    /**
     * hzero查询自身信息的接口是 GET /hzero/v1/users/self
     * 书写时间: 2020-06-01
     *
     * @return 用户信息
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 查询自身基础信息")
    @GetMapping(value = "/v1/users/self")
    public ResponseEntity<UserVO> elderSelectSelf() {
        LOGGER.debug("CompatibleController: API self is called...");
        UserVO result = userRepository.selectSelf();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("CompatibleController: API self is called successfully. The result user is {}...", result == null ? null : JSONObject.toJSONString(result));
        }
        return Results.success(result);
    }


    /**
     * 查询项目及角色列表
     * 书写时间: 2020-06-01
     */
    @Permission(permissionLogin = true)
    @ApiOperation("根据id分页获取项目列表和角色")
    @GetMapping("/v1/users/{id}/project_roles")
    @CustomPageRequest
    public ResponseEntity<PageInfoVO<ProjectDTO>> pagingQueryProjectAndRolesById(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @Encrypt @PathVariable("id") Long id,
            @RequestParam(value = "params", required = false) String[] params) {
        LOGGER.debug("CompatibleController: API project_roles is called...");
        Page<ProjectDTO> rawResult = userC7nService.pagingQueryProjectAndRolesById(pageRequest, id, ParamUtils.arrToStr(params));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("CompatibleController: API project_roles is called successfully. And the rawResult size is {}...", (rawResult == null) ? null : (rawResult.getContent() == null ? null : rawResult.getContent().size()));
        }
        return ResponseEntity.ok(new PageInfoVO<>(rawResult == null ? null : rawResult.getContent()));
    }
}
