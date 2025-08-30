package com.example.KaanBrokage.dto;


import org.springframework.data.domain.Page;
import java.util.List;


public class PageResponse<T> {
    public List<T> content;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;


    public static <T> PageResponse<T> of(Page<T> page){
        PageResponse<T> r = new PageResponse<>();
        r.content = page.getContent();
        r.page = page.getNumber();
        r.size = page.getSize();
        r.totalElements = page.getTotalElements();
        r.totalPages = page.getTotalPages();
        return r;
    }
}