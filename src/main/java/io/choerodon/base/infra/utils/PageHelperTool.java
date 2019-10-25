package io.choerodon.base.infra.utils;

import com.github.pagehelper.PageInfo;


public class PageHelperTool {

    public static <T> PageInfo<T> initPageInfoObj(int currentPage, int total, int pageSize, PageInfo<T> pageInfo) {
        pageInfo.setNextPage(currentPage < ((total + pageSize - 1) / pageSize) ? currentPage + 1 : currentPage);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(currentPage);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages((total + pageSize - 1) / pageSize);
        pageInfo.setPrePage(currentPage > 1 ? currentPage - 1 : currentPage);
        pageInfo.setIsFirstPage(currentPage == 1 ? true : false);
        pageInfo.setIsLastPage(currentPage == (total + pageSize - 1) / pageSize ? true : false);
        pageInfo.setHasPreviousPage(currentPage == 1 ? false : true);
        pageInfo.setHasNextPage(currentPage == (total + pageSize - 1) / pageSize ? false : true);
        return calcNavigatepageNums(pageInfo);
    }
 
    private static <T> PageInfo<T> calcNavigatepageNums(PageInfo<T> pageInfo) {
        //当总页数小于或等于导航页码数时
        if (pageInfo.getPages() <= pageInfo.getNavigatePages()) {
            pageInfo.setNavigatepageNums(new int[pageInfo.getPages()]);
            for (int i = 0; i < pageInfo.getPages(); i++) {
                pageInfo.getNavigatepageNums()[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            pageInfo.setNavigatepageNums(new int[pageInfo.getNavigatePages()]);
            int startNum = pageInfo.getPageNum() - pageInfo.getNavigatePages() / 2;
            int endNum = pageInfo.getPageNum() + pageInfo.getNavigatePages() / 2;
 
            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < pageInfo.getNavigatePages(); i++) {
                    pageInfo.getNavigatepageNums()[i] = startNum++;
                }
            } else if (endNum > pageInfo.getPages()) {
                endNum = pageInfo.getPages();
                //最后navigatePages页
                for (int i = pageInfo.getNavigatePages() - 1; i >= 0; i--) {
                    pageInfo.getNavigatepageNums()[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < pageInfo.getNavigatePages(); i++) {
                    pageInfo.getNavigatepageNums()[i] = startNum++;
                }
            }
        }
        return pageInfo;
    }
}