package com.ykm.server.controller.mng.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/8/5-15:21
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class MngUserDto {

    private Integer id;

    private String account;

    private String mobile;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
