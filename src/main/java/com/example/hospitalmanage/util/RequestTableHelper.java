package com.example.hospitalmanage.util;

import com.example.hospitalmanage.dto.RequestTabel;
import org.apache.commons.lang3.StringUtils;


public final class RequestTableHelper {

    public static final String ID_DEFAULT_COLUMN = "id";
    public static final String SORT_DEFAULT = "asc";
    public static final int PAGE_DEFAULT = 1;
    public static final int SIZE_DEFAULT = 5;

    private RequestTableHelper() {}

    public static void init(RequestTabel request) {
        if (StringUtils.isEmpty(request.getColumn())) {
            request.setColumn(ID_DEFAULT_COLUMN);
        }
        if (request.getPage() <= 0) {
            request.setPage(PAGE_DEFAULT);
        }
        if (request.getSize() <= 0) {
            request.setSize(SIZE_DEFAULT);
        }
        if (StringUtils.isEmpty(request.getSort())) {
            request.setSort(SORT_DEFAULT);
        }
    }

}
