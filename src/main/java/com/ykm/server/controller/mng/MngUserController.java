package com.ykm.server.controller.mng;

import com.google.gson.Gson;
import com.ykm.server.common.Authority;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.MngUserDto;
import com.ykm.server.entity.mng.MngUser;
import com.ykm.server.service.mng.MngUserService;
import com.ykm.server.utils.CookiesUtil;
import com.ykm.server.utils.ImageCaptchaService;
import com.ykm.server.utils.JedisOpsUtil;
import com.ykm.server.utils.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/8/5-15:18
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@RestController("/mng/user")
@RequestMapping("/mng/user")
public class MngUserController {

    private static Gson gson = new Gson();

    @Autowired
    MngUserService mngUserService;

    @Autowired
    CookiesUtil cookiesUtil;

    @Autowired
    JedisOpsUtil jedisOpsUtil;

    @Value("${redis.store.db}")
    Integer db;

    @Autowired
    ImageCaptchaService imageCaptchaService;


    @RequestMapping("/login")
    public ResponseDto<MngUserDto> login(String username, String password, String verifyCode, String codeId, HttpServletResponse response) {
        boolean verify = imageCaptchaService.validChallengeById(codeId, verifyCode);
        if(!verify){
            return new ResponseDto<>(null, -1, "验证码错误");
        }
        MngUserDto mngUserDto = new MngUserDto();
        MngUser mngUser = mngUserService.findByAccountAndPasswd(username, password);
        if(mngUser == null){
            return new ResponseDto<>(null, -1, "用户名或密码错误");
        }
        BeanUtils.copyProperties(mngUser, mngUserDto);
        String sid = MD5Util.md5(String.valueOf(System.currentTimeMillis()));
        jedisOpsUtil.hset(db,"mng_session", sid, gson.toJson(mngUserDto));
        Cookie cookie = cookiesUtil.genCookie("sweet_mng_sid", sid);
        response.addCookie(cookie);
        return new ResponseDto<>(mngUserDto, ResponseCode.OK);
    }

    @RequestMapping("/logout")
    public ResponseDto<String> login(HttpServletResponse response) {
        Cookie cookie = cookiesUtil.genCookie("sweet_mng_sid", "");
        response.addCookie(cookie);
        return new ResponseDto<>(null, ResponseCode.OK);
    }

    @Authority(value = "mng")
    @RequestMapping("/getUserInfo")
    public ResponseDto<MngUserDto>  getUserInfo(HttpServletRequest request) {
        String sid = cookiesUtil.getValue("sweet_mng_sid", request.getCookies());
        if(sid == null){
            return new ResponseDto<>(null, ResponseCode.Fail);
        }
        MngUserDto mngUserDto = jedisOpsUtil.hget(db, "mng_session", sid , MngUserDto.class);
        if(null == mngUserDto){
            return new ResponseDto<>(null, ResponseCode.Fail);
        }
        return new ResponseDto<>(mngUserDto, ResponseCode.OK);
    }

    @GetMapping(value = "/code")
    public void get(String id, HttpServletResponse response) throws IOException {
        BufferedImage image = imageCaptchaService.getImageChallengeById(id);

        if (image == null) {
            response.sendError(500, "get image failed.");
        }

        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
    }
}
