package io.choerodon.iam.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 此接口是为了兼容0.21版本以前创建的prometheus，获取用户信息的接口
 * @author lihao
 */
@Api(tags = SwaggerApiConfig.USER_SELF)
@RequestMapping("/v1")
@RestController
public class ElderUserController {

    @Autowired
    private UserRepository userRepository;

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 查询自身基础信息")
    @GetMapping(value = "/users/self")
    public ResponseEntity<UserVO> elderSelectSelf() {
        return Results.success(userRepository.selectSelf());
    }
}
