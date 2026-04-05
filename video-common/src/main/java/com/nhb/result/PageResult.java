package com.nhb.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页返回结果
 * @param <T> 分页数据的实体类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页条数
     */
    private long pageSize;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 从 MyBatis-Plus 的 IPage 直接构建分页结果
     * @param page MP 分页对象
     */
    public PageResult(IPage<T> page) {
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.records = page.getRecords();
    }
}
