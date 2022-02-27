package com.example.hospitalmanage.dto;

public interface RequestTabel<T> {

        String getColumn();
        void setColumn(String column);

        String getSort();
        void setSort(String sort);

        int getPage();
        void setPage(int page);

        int getSize();
        void setSize(int size);
}
