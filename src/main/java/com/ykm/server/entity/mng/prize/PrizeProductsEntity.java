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
@Table(name="prize_products")
public class PrizeProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prize_id")
    private Integer prizeId;

    @Column(name = "prize_title")
    private String prizeTitle;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_total")
    private Integer productTotal;

    @Column(name = "product_icon")
    private String productIcon;

    @Column(name = "win_bg_img_url")
    private String winBgImgUrl;

    @Column(name = "hit_rate")
    private Integer hitRate;

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

    public Integer getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Integer prizeId) {
        this.prizeId = prizeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(Integer productTotal) {
        this.productTotal = productTotal;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public Integer getHitRate() {
        return hitRate;
    }

    public void setHitRate(Integer hitRate) {
        this.hitRate = hitRate;
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

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }


    public String getWinBgImgUrl() {
        return winBgImgUrl;
    }

    public void setWinBgImgUrl(String winBgImgUrl) {
        this.winBgImgUrl = winBgImgUrl;
    }
}
