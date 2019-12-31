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
@Table(name="user_prize_list")
public class UserPrizeListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prize_id")
    private Integer prizeId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "prize_title")
    private String prizeTitle;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "goods_id")
    private Integer goodId;

    @Column(name = "goods_title")
    private String goodsTitle;


    @Column(name = "goods_pic")
    private String goodsPic;

    @Column(name = "target_url")
    private String targetUrl;


    @Column(name = "get_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date getTime = new Date();

    @Column(name = "expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime = new Date();

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

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
