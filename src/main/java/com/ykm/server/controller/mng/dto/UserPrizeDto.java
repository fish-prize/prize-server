package com.ykm.server.controller.mng.dto;

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

public class UserPrizeDto {

    private Integer id;

    private boolean exipred;
    private Integer prizeId;


    private String deviceId;


    private String prizeTitle;


    private Integer productId;


    private String productName;


    private Integer goodId;


    private String goodsTitle;


    private String goodsPic;

    private String targetUrl;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date getTime = new Date();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date exipreTime = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Integer prizeId) {
        this.prizeId = prizeId;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsPic() {
        return goodsPic;
    }

    public void setGoodsPic(String goodsPic) {
        this.goodsPic = goodsPic;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public Date getExipreTime() {
        return exipreTime;
    }

    public void setExipreTime(Date exipreTime) {
        this.exipreTime = exipreTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isExipred() {
        return exipred;
    }

    public void setExipred(boolean exipred) {
        this.exipred = exipred;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
