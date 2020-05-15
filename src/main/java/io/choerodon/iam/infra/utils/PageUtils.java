package io.choerodon.iam.infra.utils;

import io.choerodon.core.domain.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 根据page, size参数获取数据库start的行
 */
public class PageUtils {

    private PageUtils() {
    }

    public static int getBegin(int page, int size) {
        page = page <= 1 ? 1 : page;
        return (page - 1) * size;
    }

    public static List<String> getPageableSorts(Pageable Pageable) {
        List<String> sorts = new ArrayList<>();
        Iterator<Sort.Order> sortIterator = Pageable.getSort().iterator();
        while (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
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
    public static <T> Page<T> createPageFromList(List<T> all, Pageable Pageable) {
        Page<T> result = new Page<>();
        boolean queryAll = Pageable.getPageNumber() == 0 || Pageable.getPageSize() == 0;
        result.setSize(queryAll ? all.size() : Pageable.getPageSize());
        result.setNumber(Pageable.getPageNumber());
        result.setTotalElements(all.size());
        result.setTotalPages(queryAll ? 1 : (int) (Math.ceil(all.size() / (Pageable.getPageSize() * 1.0))));
        int fromIndex = Pageable.getPageSize() * (Pageable.getPageNumber() - 1);
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + Pageable.getPageSize()) {
                size = all.size() - fromIndex;
            } else {
                size = Pageable.getPageSize();
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

    /**
     * 复制分页参数并重新设置内容
     *
     * @param rpage
     * @param list
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Page<T> copyPropertiesAndResetContent(Page<R> rpage, List<T> list) {
        Page<T> tPage = new Page<>();
        BeanUtils.copyProperties(rpage, tPage);
        tPage.setContent(list);
        return tPage;
    }
}
