package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.validator.LovValidator;
import io.choerodon.base.app.service.LovService;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.web.util.PageableHelper;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class LovServiceImpl implements LovService {
    private RouteMapper routeMapper;
    private LovMapper lovMapper;
    private LovGridFieldMapper lovGridFieldMapper;
    private LovQueryFieldMapper lovQueryFieldMapper;
    private PermissionMapper permissionMapper;
    private PromptMapper promptMapper;

    private static final String PERMISSION_TYPE = "api";

    public LovServiceImpl(RouteMapper routeMapper, LovMapper lovMapper, LovGridFieldMapper lovGridFieldMapper, LovQueryFieldMapper lovQueryFieldMapper, PermissionMapper permissionMapper, PromptMapper promptMapper) {
        this.routeMapper = routeMapper;
        this.lovMapper = lovMapper;
        this.lovGridFieldMapper = lovGridFieldMapper;
        this.lovQueryFieldMapper = lovQueryFieldMapper;
        this.permissionMapper = permissionMapper;
        this.promptMapper = promptMapper;
    }

    @Override
    public LovDTO queryLovByCode(String code) {
        LovDTO example = new LovDTO();
        example.setCode(code);
        LovDTO result = lovMapper.selectOne(example);
        if (result == null) {
            throw new CommonException("error.lov.notFound");
        }
        PermissionDTO permissionExample = new PermissionDTO();
        permissionExample.setCode(result.getPermissionCode());
        PermissionDTO permission = permissionMapper.selectOne(permissionExample);
        if (permission != null) {
            RouteDTO routeExample = new RouteDTO();
            routeExample.setServiceCode(permission.getServiceCode());
            RouteDTO routeResult = routeMapper.selectOne(routeExample);
            if (routeResult != null) {
                result.setUrl(routeResult.getBackendPath().substring(0, routeResult.getBackendPath().length() - 3) + permission.getPath());
            }
            result.setMethod(permission.getMethod());
        }
        LovGridFieldDTO gridExample = new LovGridFieldDTO();
        gridExample.setLovCode(result.getCode());
        List<LovGridFieldDTO> gridFieldDTOList = lovGridFieldMapper.select(gridExample);
        Collections.sort(gridFieldDTOList, (o1, o2) -> {
            Double diff = o1.getGridFieldOrder() - o2.getGridFieldOrder();
            if (diff > 0) {
                return -1;
            } else if (diff < 0) {
                return 1;
            }
            return 0;
        });
        result.setGridFields(getGridByLang(gridFieldDTOList));
        LovQueryFieldDTO queryExample = new LovQueryFieldDTO();
        queryExample.setLovCode(result.getCode());
        result.setQueryFields(lovQueryFieldMapper.select(queryExample));
        // 多语言适配标题
        PromptDTO prompt = getPrompt(result.getTitle());
        if (!ObjectUtils.isEmpty(prompt)) {
            result.setTitle(prompt.getDescription());
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LovDTO createLov(LovDTO lovDTO) {
        lovDTO.setId(null);
        List<LovGridFieldDTO> gridFields = lovDTO.getGridFields();
        List<LovQueryFieldDTO> queryFields = lovDTO.getQueryFields();
        LovValidator.checkTree(lovDTO);
//        往 fd_lov 表插入数据
        if (lovMapper.insertSelective(lovDTO) != 1) {
            throw new CommonException("error.lov.insert");
        }
//        往 fd_grid_field 表插入数据
        if (!ObjectUtils.isEmpty(gridFields)) {
            for (LovGridFieldDTO lovGridFieldDTO : gridFields) {
                LovValidator.checkGridEmpty(lovGridFieldDTO, queryFields);
                lovGridFieldDTO.setId(null);
                lovGridFieldDTO.setLovCode(lovDTO.getCode());
                if (lovGridFieldMapper.insertSelective(lovGridFieldDTO) != 1) {
                    throw new CommonException("error.lov.grid.insert");
                }
            }
        }
//        往 fd_lov_query_field 表插入数据
        if (!ObjectUtils.isEmpty(queryFields)) {
            for (LovQueryFieldDTO lovQueryFieldDTO : queryFields) {
                LovValidator.checkQueryEmpty(lovQueryFieldDTO);
                lovQueryFieldDTO.setId(null);
                lovQueryFieldDTO.setLovCode(lovDTO.getCode());

                if (lovQueryFieldMapper.insertSelective(lovQueryFieldDTO) != 1) {
                    throw new CommonException("error.lov.query.insert");
                }
            }
        }
        return lovDTO;
    }

    @Override
    public PageInfo<List<PermissionDTO>> queryApiByLevel(Pageable pageable, String level, String params) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() -> permissionMapper.queryByLevelAndCode(level, params));
    }

    @Override
    public PageInfo<LovDTO> queryLovList(Pageable pageable, String code, String description, String level, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(() -> lovMapper.selectLovList(code, description, level, param));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LovDTO updateLov(Long id, LovDTO lovDTO) {
//        传入需要更新的值
        List<LovGridFieldDTO> gridFieldDTOS = lovDTO.getGridFields();
        List<LovQueryFieldDTO> queryFieldDTOS = lovDTO.getQueryFields();
//        数据库存储的值
        List<LovGridFieldDTO> dbGridFieldDTOS = lovGridFieldMapper.select(new LovGridFieldDTO(lovDTO.getCode()));

        lovDTO.setId(id);
        LovDTO dbLovDTO = lovMapper.selectByPrimaryKey(lovDTO);
        if (ObjectUtils.isEmpty(dbLovDTO)) {
            throw new CommonException("error.lov.not.exist");
        }
        LovValidator.checkTree(lovDTO);
        lovDTO.setObjectVersionNumber(dbLovDTO.getObjectVersionNumber());
//        更新 fd_lov
        if (lovMapper.updateByPrimaryKey(lovDTO) != 1) {
            throw new CommonException("error.lov.update");
        }
//        更新 fd_grid_field
        if (!ObjectUtils.isEmpty(gridFieldDTOS)) {
            for (LovGridFieldDTO lovGridFieldDTO : gridFieldDTOS) {
//                如果 id 为 null 则插入
                if (lovGridFieldDTO.getId() == null && "add".equals(lovGridFieldDTO.get__status())) {
                    LovValidator.checkGridEmpty(lovGridFieldDTO, queryFieldDTOS);
                    lovGridFieldDTO.setLovCode(lovDTO.getCode());
                    if (lovGridFieldMapper.insertSelective(lovGridFieldDTO) != 1) {
                        throw new CommonException("error.lov.grid.insert");
                    }
                }
//                如果是否为查询字段为 false，则删除 fd_query_field 对应的数据

                for (LovGridFieldDTO dbGridFieldDTO : dbGridFieldDTOS) {
                    Boolean isDelete = false;

//                    有 id，且 __status 为 delete 时，删除 grid 和 query 对应数据
                    if (dbGridFieldDTO.getId().equals(lovGridFieldDTO.getId()) && "delete".equals(lovGridFieldDTO.get__status())) {
                        LovQueryFieldDTO queryFieldDTO = new LovQueryFieldDTO();
                        queryFieldDTO.setLovCode(lovDTO.getCode());
                        queryFieldDTO.setQueryFieldName(dbGridFieldDTO.getGridFieldName());
                        if (lovGridFieldMapper.deleteByPrimaryKey(dbGridFieldDTO.getId()) != 1) {
                            throw new CommonException("error.grid.delete");
                        }

                        lovQueryFieldMapper.delete(queryFieldDTO);
//                        表示已经删除了 query 中的数据
                        isDelete = true;
                    }

                    if (!isDelete) {
                        if (BooleanUtils.isFalse(lovGridFieldDTO.getGridFieldQueryFlag())) {
                            LovQueryFieldDTO queryFieldDTO = new LovQueryFieldDTO();
                            queryFieldDTO.setLovCode(lovDTO.getCode());
                            queryFieldDTO.setQueryFieldName(lovGridFieldDTO.getGridFieldName());
                            lovQueryFieldMapper.delete(queryFieldDTO);
                        }
                    }
                    if (dbGridFieldDTO.getId().equals(lovGridFieldDTO.getId()) && "update".equals(lovGridFieldDTO.get__status())) {
                        dbGridFieldDTO.setGridFieldAlign(lovGridFieldDTO.getGridFieldAlign());
                        dbGridFieldDTO.setGridFieldDisplayFlag(lovGridFieldDTO.getGridFieldDisplayFlag());
                        dbGridFieldDTO.setGridFieldLabel(lovGridFieldDTO.getGridFieldLabel());
                        dbGridFieldDTO.setGridFieldName(lovGridFieldDTO.getGridFieldName());
                        dbGridFieldDTO.setGridFieldOrder(lovGridFieldDTO.getGridFieldOrder());
                        dbGridFieldDTO.setGridFieldQueryFlag(lovGridFieldDTO.getGridFieldQueryFlag());
                        dbGridFieldDTO.setGridFieldWidth(lovGridFieldDTO.getGridFieldWidth());
                        if (lovGridFieldMapper.updateByPrimaryKey(dbGridFieldDTO) != 1) {
                            throw new CommonException("error.grid.update");
                        }
                    }
                }
            }
        }
//        更新 fd_grid_field 时，query_flag 可能被更新为 false，从而会删 fd_query_field 表数据，所以在此处获取数据库中的数据
        List<LovQueryFieldDTO> dbQueryFieldDTOS = lovQueryFieldMapper.select(new LovQueryFieldDTO(lovDTO.getCode()));
//        更新 fd_query_field
        if (!ObjectUtils.isEmpty(queryFieldDTOS)) {
            for (LovQueryFieldDTO lovQueryFieldDTO : queryFieldDTOS) {
                LovValidator.checkQueryEmpty(lovQueryFieldDTO);
                if (lovQueryFieldDTO.getId() == null) {
                    lovQueryFieldDTO.setLovCode(lovDTO.getCode());

                    if (lovQueryFieldMapper.insertSelective(lovQueryFieldDTO) != 1) {
                        throw new CommonException("error.lov.query.insert");
                    }
                }

                for (LovQueryFieldDTO dbQueryFieldDTO : dbQueryFieldDTOS) {
                    if (dbQueryFieldDTO.getId().equals(lovQueryFieldDTO.getId())) {
                        dbQueryFieldDTO.setQueryFieldRequiredFlag(lovQueryFieldDTO.getQueryFieldRequiredFlag());
                        dbQueryFieldDTO.setQueryFieldParamType(lovQueryFieldDTO.getQueryFieldParamType());
                        dbQueryFieldDTO.setQueryFieldType(lovQueryFieldDTO.getQueryFieldType());
                        dbQueryFieldDTO.setQueryFieldWidth(lovQueryFieldDTO.getQueryFieldWidth());
                        dbQueryFieldDTO.setQueryFieldOrder(lovQueryFieldDTO.getQueryFieldOrder());
                        dbQueryFieldDTO.setQueryFieldDisplayFlag(lovQueryFieldDTO.getQueryFieldDisplayFlag());
                        dbQueryFieldDTO.setQueryFieldLookupCode(lovQueryFieldDTO.getQueryFieldLookupCode());
                        dbQueryFieldDTO.setQueryFieldLovCode(lovQueryFieldDTO.getQueryFieldLovCode());
                        dbQueryFieldDTO.setQueryFieldLabel(lovQueryFieldDTO.getQueryFieldLabel());
                        if (lovQueryFieldMapper.updateByPrimaryKeySelective(dbQueryFieldDTO) != 1) {
                            throw new CommonException("error.query.update");
                        }
                    }
                }
            }
        }
        return lovDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteLov(Long id) {
        LovDTO lovDTO = lovMapper.selectByPrimaryKey(id);
        if (lovMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.lov.delete");
        }

        lovGridFieldMapper.delete(new LovGridFieldDTO(lovDTO.getCode()));

        lovQueryFieldMapper.delete(new LovQueryFieldDTO(lovDTO.getCode()));
    }

    /**
     * 查询Prompt
     *
     * @param code
     * @return
     */
    private PromptDTO getPrompt(String code) {
        String lang = DetailsHelper.getUserDetails().getLanguage();
        return promptMapper.selectOne(new PromptDTO().setPromptCode(code).setLang(lang));
    }

    /**
     * 维护列属性的多语言
     *
     * @param gridFieldDTOList
     * @return
     */
    private List<LovGridFieldDTO> getGridByLang(List<LovGridFieldDTO> gridFieldDTOList) {
        if (CollectionUtils.isEmpty(gridFieldDTOList)) {
            return gridFieldDTOList;
        }
        gridFieldDTOList.forEach(g -> {
            PromptDTO prompt = getPrompt(g.getGridFieldName());
            if (!ObjectUtils.isEmpty(prompt)) {
                g.setGridFieldName(prompt.getDescription());
            }
        });
        return gridFieldDTOList;
    }
}