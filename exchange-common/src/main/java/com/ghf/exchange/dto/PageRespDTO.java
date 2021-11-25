package com.ghf.exchange.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRespDTO<T> implements Serializable {

    @ApiModelProperty(value = "总记录数")
    private int total;

    @ApiModelProperty(value = "当前页集合")
    private List<T> list;

    @ApiModelProperty(value = "当前页码")
    private int pageNum;

    @ApiModelProperty(value = "每页的数量")
    private int pageSize;

    @ApiModelProperty(value = "总页数")
    private int pages;

    @ApiModelProperty(value = "当前页的数量")
    private int size;

    @ApiModelProperty(value = "当前页面第一个元素在数据库中的行号")

    private int startRow;

    @ApiModelProperty(value = "当前页面最后一个元素在数据库中的行号")
    private int endRow;

    public PageRespDTO(int pageNum, int pageSize, int total) {
        this(pageNum, pageSize, total, new ArrayList<>());
    }

    public PageRespDTO(int pageNum, int pageSize, int total, List<T> list) {
        if (pageSize < 1) {
            pageSize = 1;
        }
        if (total < 0) {
            total = 0;
        }
        this.pageSize = pageSize;
        this.total = total;
        int pageCount = this.total / this.pageSize;
        if (this.total % this.pageSize != 0) {
            pageCount++;
        }
        if (pageCount < 1) {
            pageCount = 1;
        }
        this.pages = pageCount;
        if (pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.pages) {
            pageNum = this.pages;
        }
        this.pageNum = pageNum;
        if (total > 0) {
            this.startRow = (pageNum - 1) * pageSize + 1;
            this.endRow = Math.min(this.startRow + pageSize - 1, Math.max(total, this.startRow));
            this.size = this.endRow - this.startRow + 1;
        } else {
            this.startRow = 0;
            this.endRow = 0;
            this.size = 0;
        }
        this.setList(list);
        this.setSize(list.size());
    }

}
