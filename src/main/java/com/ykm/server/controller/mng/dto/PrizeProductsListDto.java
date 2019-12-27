package com.ykm.server.controller.mng.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 广州引力网络科技有限公司
 * 功能：
 * 日期：2019/8/8-13:46
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class PrizeProductsListDto {
    private List<PrizeProductsDto> content = new ArrayList<>();
    private Long total;

    public List<PrizeProductsDto> getContent() {
        return content;
    }

    public void setContent(List<PrizeProductsDto> content) {
        this.content = content;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}