package com.example.hospitalmanage.dto;

import com.example.hospitalmanage.model.Treatment;

import java.util.List;

public interface ResponseTable <T>{

    public int getAllItemsSize();
    public void setAllItemsSize(int allItemsSize);

    public int getPage();
    public void setPage(int page);

    public int getTotalPages();
    public void setTotalPages(int totalPages);

    public int getSize();
    public void setSize(int size);

    public List<T> getContent();
    public void setContent(List<T> content);

    public String getSort();
    public void setSort(String sort);

    public String getColumnSort();
    public void setColumnSort(String columnSort);
}
