package io.choerodon.base.app.service.impl;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.choerodon.base.api.dto.DashboardPositionDTO;
import io.choerodon.base.app.service.DashboardService;
import io.choerodon.base.infra.dto.DashboardDTO;
import io.choerodon.base.infra.dto.DashboardRoleDTO;
import io.choerodon.base.infra.dto.UserDashboardDTO;
import io.choerodon.base.infra.mapper.DashboardMapper;
import io.choerodon.base.infra.mapper.DashboardRoleMapper;
import io.choerodon.base.infra.mapper.UserDashboardMapper;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;

/**
 * @author dongfan117@gmail.com
 */
@Service("dashboardService")
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private DashboardMapper dashboardMapper;
    private DashboardRoleMapper dashboardRoleMapper;
    private UserDashboardMapper userDashboardMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    public DashboardServiceImpl(DashboardMapper dashboardMapper,
                                DashboardRoleMapper dashboardRoleMapper,
                                UserDashboardMapper userDashboardMapper) {
        this.dashboardMapper = dashboardMapper;
        this.dashboardRoleMapper = dashboardRoleMapper;
        this.userDashboardMapper = userDashboardMapper;
    }

    @Override
    public DashboardDTO update(Long dashboardId, DashboardDTO dashboardDTO, Boolean updateRole) {
        dashboardDTO.setId(dashboardId);
        dashboardDTO.setPosition(convertPositionToJson(dashboardDTO.getPositionDTO()));
        if (dashboardMapper.updateByPrimaryKeySelective(dashboardDTO) != 1) {
            throw new UpdateException("error.dashboard.not.exist");
        }
        DashboardDTO dashboard =
                modelMapper.map(dashboardMapper.selectByPrimaryKey(dashboardId), DashboardDTO.class);
        if (!updateRole && dashboard.getNeedRoles() != null && !dashboard.getNeedRoles()) {
            return dashboard;
        }
        List<String> roleCodes = dashboardDTO.getRoleCodes();
        if (roleCodes != null && !roleCodes.isEmpty()) {
            dashboardRoleMapper.deleteByDashboardCode(dashboard.getCode());
            for (String role : roleCodes) {
                DashboardRoleDTO dto = new DashboardRoleDTO();
                dto.setRoleCode(role);
                dto.setDashboardCode(dashboard.getCode());
                dashboardRoleMapper.insertSelective(dto);
            }
        }
        dashboard.setRoleCodes(dashboardRoleMapper.selectRoleCodes(dashboard.getCode()));
        return dashboard;
    }

    @Override
    public DashboardDTO query(Long dashboardId) {
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setId(dashboardId);
        dashboard = dashboardMapper.selectByPrimaryKey(dashboardId);

        if (dashboard == null) {
            throw new NotExistedException("error.dashboard.not.exist");
        }
        return modelMapper.map(dashboard, DashboardDTO.class);
    }

    @Override
    public PageInfo<DashboardDTO> list(DashboardDTO dashboardDTO, Pageable pageable, String param) {
        PageInfo<DashboardDTO> pageInfo =
                PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                        .doSelectPageInfo(() -> dashboardMapper.fulltextSearch(dashboardDTO, param));
        pageInfo.getList().forEach(dashboard -> {
            List<String> roleCodes = dashboardMapper.selectRoleCodesByDashboard(dashboard.getCode(), dashboard.getLevel());
            dashboard.setRoleCodes(roleCodes);
        });
        return pageInfo;
    }

    @Override
    public void reset(Long dashboardId) {
        UserDashboardDTO deleteCondition = new UserDashboardDTO();
        deleteCondition.setSourceId(dashboardId);
        long num = userDashboardMapper.delete(deleteCondition);
        LOGGER.info("reset userDashboard by dashboardId: {}, delete num: {}", dashboardId, num);
    }

    private String convertPositionToJson(DashboardPositionDTO positionDTO) {
        if (positionDTO == null ||
                (positionDTO.getHeight() == null && positionDTO.getWidth() == null)) {
            return null;
        }
        if (positionDTO.getPositionX() == null) {
            positionDTO.setPositionX(0);
        }
        if (positionDTO.getPositionY() == null) {
            positionDTO.setPositionY(0);
        }
        if (positionDTO.getHeight() == null) {
            positionDTO.setHeight(0);
        }
        if (positionDTO.getWidth() == null) {
            positionDTO.setWidth(0);
        }
        try {
            return objectMapper.writeValueAsString(positionDTO);
        } catch (JsonProcessingException e) {
            LOGGER.warn("error.userDashboardService.convertPositionToJson.JsonProcessingException", e);
            return null;
        }
    }
}
