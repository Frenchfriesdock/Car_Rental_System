package com.hosiky.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;        // 总记录数
    private List<T> records;   // 当前页数据列表
    private Long current;      // 当前页码
    private Long size;         // 每页大小
    private Long pages;        // 总页数

    public PageResult() {
    }

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public PageResult(Long total, List<T> records, Long current, Long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size; // 计算总页数
    }
}