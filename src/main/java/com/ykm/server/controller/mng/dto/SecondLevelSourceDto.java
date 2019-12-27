package com.ykm.server.controller.mng.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 广州引力网络科技有限公司
 * 功能：
 * 日期：2019/8/9-17:15
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class SecondLevelSourceDto {

    private String value;
    private String label;
    private List<SourceDto> children = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<SourceDto> getChildren() {
        return children;
    }

    public void setChildren(List<SourceDto> children) {
        this.children = children;
    }
}
