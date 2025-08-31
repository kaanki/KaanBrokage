package com.example.KaanBrokage.dto;


import org.springframework.data.domain.Page;

import java.util.List;


public class ListOrdersResponse<T> {
    public List<T> content;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;


    public static <T> ListOrdersResponse<T> of(Page<T> page) {
        ListOrdersResponse<T> r = new ListOrdersResponse<>();
        r.content = page.getContent();
        r.page = page.getNumber();
        r.size = page.getSize();
        r.totalElements = page.getTotalElements();
        r.totalPages = page.getTotalPages();
        return r;
    }
}