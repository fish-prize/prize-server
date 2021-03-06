package com.ykm.server.entity.mng.prize;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/24-13:48
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Entity
@Table(name="prize_activity")
public class PrizeActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prize_title")
    private String prizeTitle;

    @Column(name = "app_key")
    private String appKey;

    @Column(name = "invalid_content")
    private String invalidContent;

    @Column(name = "service_phone")
    private String servicePhone;

    @Column(name = "rules")
    private String rules;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getInvalidContent() {
        return invalidContent;
    }

    public void setInvalidContent(String invalidContent) {
        this.invalidContent = invalidContent;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
