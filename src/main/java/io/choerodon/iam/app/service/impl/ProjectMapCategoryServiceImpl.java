package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.ProjectMapCategoryService;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;

@Service
public class ProjectMapCategoryServiceImpl implements ProjectMapCategoryService {

    @Autowired
    private ProjectMapCategoryMapper projectMapCategoryMapper;

    @Override
    public List<Long> listProjectIdsByCategoryId(Long categoryId) {
        return projectMapCategoryMapper.listProjectIdsByCategoryId(categoryId);
    }
}
