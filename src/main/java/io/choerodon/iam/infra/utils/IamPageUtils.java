package io.choerodon.iam.infra.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author zmf
 * @since 20-4-21
 */
public class IamPageUtils {
    private IamPageUtils() {
    }

    public static <T> Page<T> createEmptyPage(int page, int size) {
        Page result = new Page();
        result.setNumber(page);
        result.setSize(size);
        return (Page<T>) result;
    }

    public static int getBegin(int page, int size) {
        page = page <= 1 ? 1 : page;
        return (page - 1) * size;
    }

    public static List<String> getPageableSorts(Pageable Pageable) {
        List<String> sorts = new ArrayList<>();
        for (Sort.Order order : Pageable.getSort()) {
            sorts.add(order.getProperty() + "," + order.getDirection());
        }
        return sorts;
    }

    /**
     * 装配Page对象
     *
     * @param all      包含所有内容的列表
     * @param Pageable 分页参数
     * @return PageInfo
     */
    public static <T> Page<T> createPageFromList(List<T> all, PageRequest Pageable) {
        Page<T> result = new Page<>();
        boolean queryAll = Pageable.getPage() == 0 || Pageable.getSize() == 0;
        result.setSize(queryAll ? all.size() : Pageable.getSize());
        result.setNumber(Pageable.getPage());
        result.setTotalPages(all.size());
        result.setTotalPages(queryAll ? 1 : (int) (Math.ceil(all.size() / (Pageable.getSize() * 1.0))));
        int fromIndex = Pageable.getSize() * (Pageable.getPage() - 1);
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + Pageable.getSize()) {
                size = all.size() - fromIndex;
            } else {
                size = Pageable.getSize();
            }
            result.setSize(queryAll ? all.size() : size);
            result.setContent(queryAll ? all : all.subList(fromIndex, fromIndex + result.getSize()));
        } else {
            size = 0;
            result.setSize(queryAll ? all.size() : size);
            result.setContent(new ArrayList<>());
        }
        return result;
    }
}
