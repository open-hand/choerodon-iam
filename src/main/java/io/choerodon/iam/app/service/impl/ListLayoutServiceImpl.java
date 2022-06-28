package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ListLayoutColumnRelVO;
import io.choerodon.iam.api.vo.ListLayoutVO;
import io.choerodon.iam.app.service.ListLayoutService;
import io.choerodon.iam.infra.dto.ListLayoutColumnRelDTO;
import io.choerodon.iam.infra.dto.ListLayoutDTO;
import io.choerodon.iam.infra.mapper.ListLayoutColumnRelMapper;
import io.choerodon.iam.infra.mapper.ListLayoutMapper;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ListLayoutServiceImpl implements ListLayoutService {
    @Autowired
    private ListLayoutMapper listLayoutMapper;
    @Autowired
    private ListLayoutColumnRelMapper listLayoutColumnRelMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ListLayoutVO save(Long organizationId, Long projectId, ListLayoutVO listLayoutVO) {
        if (ObjectUtils.isEmpty(listLayoutVO.getApplyType())) {
            throw new CommonException("error.list.layout.apply.type.null");
        }
        Long userId = DetailsHelper.getUserDetails().getUserId();
        ListLayoutDTO layoutDTO = new ListLayoutDTO(listLayoutVO.getApplyType(), userId, projectId, organizationId);
        List<ListLayoutDTO> layoutDTOS = listLayoutMapper.select(layoutDTO);
        if (CollectionUtils.isEmpty(layoutDTOS)) {
            baseInsert(layoutDTO);
        } else {
            layoutDTO = layoutDTOS.get(0);
        }
        saveColumnRel(organizationId, projectId, layoutDTO.getId(), listLayoutVO.getListLayoutColumnRelVOS());
        return queryByApplyType(organizationId, projectId, listLayoutVO.getApplyType());
    }

    private void saveColumnRel(Long organizationId, Long projectId, Long layoutId, List<ListLayoutColumnRelVO> listLayoutColumnRelVOS) {
        ListLayoutColumnRelDTO listLayoutColumnRelDTO = new ListLayoutColumnRelDTO();
        listLayoutColumnRelDTO.setProjectId(projectId);
        listLayoutColumnRelDTO.setOrganizationId(organizationId);
        listLayoutColumnRelDTO.setLayoutId(layoutId);
        List<ListLayoutColumnRelDTO> layoutColumnRelDTOS = listLayoutColumnRelMapper.select(listLayoutColumnRelDTO);
        if (!CollectionUtils.isEmpty(layoutColumnRelDTOS)) {
            listLayoutColumnRelMapper.delete(listLayoutColumnRelDTO);
        }
        listLayoutColumnRelVOS.forEach(v -> {
            ListLayoutColumnRelDTO layoutColumnRelDTO = modelMapper.map(v, ListLayoutColumnRelDTO.class);
            layoutColumnRelDTO.setOrganizationId(organizationId);
            layoutColumnRelDTO.setLayoutId(layoutId);
            layoutColumnRelDTO.setProjectId(projectId);
            if (listLayoutColumnRelMapper.insertSelective(layoutColumnRelDTO) != 1) {
                throw new CommonException("error.list.layout.column.rel.insert");
            }
        });
    }

    private ListLayoutDTO baseInsert(ListLayoutDTO layoutDTO) {
        if (listLayoutMapper.insertSelective(layoutDTO) != 1) {
            throw new CommonException("error.list.layout.insert");
        }
        return listLayoutMapper.selectByPrimaryKey(layoutDTO.getId());
    }

    @Override
    public ListLayoutVO queryByApplyType(Long organizationId, Long projectId, String applyType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        ListLayoutDTO listLayoutDTO = new ListLayoutDTO(applyType, userId, projectId, organizationId);
        List<ListLayoutDTO> listLayoutDTOS = listLayoutMapper.select(listLayoutDTO);
        if (CollectionUtils.isEmpty(listLayoutDTOS)) {
            return null;
        }
        ListLayoutDTO layoutDTO = listLayoutDTOS.get(0);
        ListLayoutVO layoutVO = modelMapper.map(layoutDTO, ListLayoutVO.class);
        ListLayoutColumnRelDTO listLayoutColumnRelDTO = new ListLayoutColumnRelDTO(layoutVO.getId(), projectId, organizationId);
        List<ListLayoutColumnRelDTO> list = listLayoutColumnRelMapper.select(listLayoutColumnRelDTO);
        if (!CollectionUtils.isEmpty(list)) {
            layoutVO.setListLayoutColumnRelVOS(modelMapper.map(list, new TypeToken<List<ListLayoutColumnRelVO>>() {
            }.getType()));
        }
        return layoutVO;
    }
}
