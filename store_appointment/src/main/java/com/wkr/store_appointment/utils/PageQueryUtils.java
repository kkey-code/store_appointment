package com.wkr.store_appointment.utils;

public final class PageQueryUtils {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private PageQueryUtils() {
    }

    public static long page(Integer page) {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    public static long page(int page) {
        return page < 1 ? DEFAULT_PAGE : page;
    }

    public static long pageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public static long pageSize(int pageSize) {
        return pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
    }
}
