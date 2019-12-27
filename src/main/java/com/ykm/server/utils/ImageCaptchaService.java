package com.ykm.server.utils;


import cn.apiclub.captcha.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * 图片验证码Service
 *
 * @author wenxy
 */
@Component
public class ImageCaptchaService {

    @Autowired
    private JedisOpsUtil jedisOpsUtil;

    @Value("${redis.store.db}")
    Integer db;

    private static final String CAP_REL = "verify_pic_code";

    /**
     * 根据预设的ID获取图片验证码
     *
     * @param id
     * @return
     */

    public BufferedImage getImageChallengeById(String id) {
        Captcha captcha = new Captcha.Builder(150, 48).addText().build();
        // 建立映射关系
        jedisOpsUtil.hset(db, CAP_REL, id, captcha.getAnswer());
        return captcha.getImage();
    }

    /**
     * 验证是否正确
     *
     * @param id
     * @param code
     * @return
     */
    public boolean validChallengeById(String id, String code) {
        String realCode = getRelMap(id);

        if (realCode == null) {
            return false;
        }

        return code.equals(realCode);
    }

    /**
     * 获取ID与Code（用户输入）的对应关系
     *
     * @return Code, 不存在则返回null
     */
    private String getRelMap(String id) {
        String code = jedisOpsUtil.hget(db,CAP_REL, id, String.class);
        if (code == null) {
            return null;
        }

        return code;
    }

}
