package io.choerodon.iam.app.service;

import java.util.List;

public interface ProjectMapCategoryService {
    List<Long> listProjectIdsByCategoryId(Long categoryId);
}
