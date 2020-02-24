package io.choerodon.base.api.controller.v1;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.ProjectOverViewVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.OrgSharesDTO;
import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.api.vo.ResendEmailMsgDTO;
import io.choerodon.base.app.service.CaptchaService;
import io.choerodon.base.app.service.DemoRegisterService;
import io.choerodon.base.app.service.OrganizationService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author wuguokai
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/organizations")
public class OrganizationController extends BaseController {

    private OrganizationService organizationService;

    private DemoRegisterService demoRegisterService;
    private CaptchaService captchaService;

    public OrganizationController(OrganizationService organizationService,
                                  DemoRegisterService demoRegisterService,
                                  CaptchaService captchaService) {
        this.organizationService = organizationService;
        this.demoRegisterService = demoRegisterService;
        this.captchaService = captchaService;
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "发送邮箱验证码，返回重发间隔，单位为秒")
    @GetMapping(value = "/send/email_captcha")
    public ResponseEntity<ResendEmailMsgDTO> sendEmailCaptcha(@RequestParam String email) {
        return new ResponseEntity<>(captchaService.sendEmailCaptcha(email), HttpStatus.OK);
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "验证码校验")
    @PostMapping(value = "/check/email_captcha")
    public ResponseEntity checkEmailCaptcha(@RequestParam String email,
                                            @RequestParam String captcha) {
        captchaService.validateCaptcha(email, captcha);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "校验用户邮箱是否在iam/gitlab已存在")
    @GetMapping(value = "/check/email")
    @Permission(permissionPublic = true)
    public ResponseEntity checkEmailIsExist(
            @RequestParam(value = "email") String email) {
        demoRegisterService.checkEmail(email);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 修改组织信息
     *
     * @return 修改成功后的组织信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层修改组织")
    @PutMapping(value = "/{organization_id}")
    public ResponseEntity<OrganizationDTO> update(@PathVariable(name = "organization_id") Long id,
                                                  @RequestBody @Valid OrganizationDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.updateOrganization(id, organizationDTO, ResourceLevel.SITE.value(), 0L),
                HttpStatus.OK);
    }

    /**
     * 组织层修改组织信息
     *
     * @return 修改成功后的组织信息
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层修改组织")
    @PutMapping(value = "/{organization_id}/organization_level")
    public ResponseEntity<OrganizationDTO> updateOnOrganizationLevel(@PathVariable(name = "organization_id") Long id,
                                                                     @RequestBody @Valid OrganizationDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.updateOrganization(id, organizationDTO, ResourceLevel.ORGANIZATION.value(), id),
                HttpStatus.OK);
    }


    /**
     * 根据组织id查询组织
     *
     * @param id 所要查询的组织id号
     * @return 组织信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层根据组织id查询组织")
    @GetMapping(value = "/{organization_id}")
    public ResponseEntity<OrganizationDTO> query(@PathVariable(name = "organization_id") Long id) {
        return new ResponseEntity<>(organizationService.queryOrganizationById(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层根据组织名称查询组织")
    @GetMapping(value = "/listByName")
    public ResponseEntity<List<OrganizationDTO>> queryOrganizationsByName(@RequestParam(name = "organization_name") String organizationName) {
        return new ResponseEntity<>(organizationService.queryOrganizationsByName(organizationName), HttpStatus.OK);
    }

    /**
     * 组织层根据组织id查询组织,附带该用户在该组织分配了那些角色，以及该组织下所有的项目数量
     *
     * @param id 所要查询的组织id号
     * @return 组织信息
     */
    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "组织层根据组织id查询组织，并查询被分配的角色")
    @GetMapping(value = "/{organization_id}/org_level")
    public ResponseEntity<OrganizationDTO> queryOrgLevel(@PathVariable(name = "organization_id") Long id) {
        return new ResponseEntity<>(organizationService.queryOrganizationWithRoleById(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询组织")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<OrganizationDTO>> pagingQuery(@ApiIgnore
                                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String code,
                                                                 @RequestParam(required = false) String ownerRealName,
                                                                 @RequestParam(required = false) Boolean enabled,
                                                                 @RequestParam(required = false) String params) {
        return new ResponseEntity<>(organizationService.pagingQuery(pageable, name, code, ownerRealName, enabled, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "分页查询所有组织基本信息")
    @GetMapping(value = "/all")
    @CustomPageRequest
    public ResponseEntity<PageInfo<OrganizationSimplifyDTO>> getAllOrgs(@ApiIgnore
                                                                        @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(organizationService.getAllOrgs(pageable), HttpStatus.OK);
    }

    /**
     * 根据id集合查询组织
     *
     * @param ids id集合，去重
     * @return 组织集合
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据id集合查询组织")
    @PostMapping("/ids")
    public ResponseEntity<List<OrganizationDTO>> queryByIds(@RequestBody Set<Long> ids) {
        return new ResponseEntity<>(organizationService.queryByIds(ids), HttpStatus.OK);
    }


    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "启用组织")
    @PutMapping(value = "/{organization_id}/enable")
    public ResponseEntity<OrganizationDTO> enableOrganization(@PathVariable(name = "organization_id") Long id) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationService.enableOrganization(id, userId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "禁用组织")
    @PutMapping(value = "/{organization_id}/disable")
    public ResponseEntity<OrganizationDTO> disableOrganization(@PathVariable(name = "organization_id") Long id) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationService.disableOrganization(id, userId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "组织信息校验")
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody OrganizationDTO organization) {
        organizationService.check(organization);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 根据organizationId和param模糊查询loginName和realName两列
     */
    @Permission(type = ResourceType.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_MEMBER)
    @ApiOperation(value = "分页模糊查询组织下的用户")
    @GetMapping(value = "/{organization_id}/users")
    @CustomPageRequest
    public ResponseEntity<PageInfo<UserDTO>> pagingQueryUsersOnOrganization(@PathVariable(name = "organization_id") Long id,
                                                                            @ApiIgnore
                                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                            @RequestParam(required = false, name = "id") Long userId,
                                                                            @RequestParam(required = false) String email,
                                                                            @RequestParam(required = false) String param) {
        return new ResponseEntity<>(organizationService.pagingQueryUsersInOrganization(id, userId, email, pageable, param), HttpStatus.OK);
    }

    @CustomPageRequest
    @PostMapping("/specified")
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据组织Id列表分页查询组织简要信息")
    public ResponseEntity<PageInfo<OrgSharesDTO>> pagingSpecified(@SortDefault(value = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                                                  @RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) String code,
                                                                  @RequestParam(required = false) Boolean enabled,
                                                                  @RequestParam(required = false) String params,
                                                                  @RequestBody Set<Long> orgIds) {
        return new ResponseEntity<>(organizationService.pagingSpecified(orgIds, name, code, enabled, params, pageable), HttpStatus.OK);
    }


    @GetMapping("/{organization_id}/project/overview")
    @Permission(type = ResourceType.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_ADMINISTRATOR)
    @ApiOperation(value = "组织概览，返回启用项目数量和停用项目数量")
    public ResponseEntity<ProjectOverViewVO> projectOverview(
            @PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(organizationService.projectOverview(organizationId), HttpStatus.OK);
    }
    
}
