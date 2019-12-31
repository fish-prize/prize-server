package com.ykm.server.controller.prize;

import com.aliyun.oss.common.utils.DateUtil;
import com.ykm.server.common.exceptions.BusinessException;
import com.ykm.server.common.exceptions.MessageCode;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.*;
import com.ykm.server.entity.mng.prize.PrizeActivityEntity;
import com.ykm.server.entity.mng.prize.PrizeGoodsEntity;
import com.ykm.server.entity.mng.prize.PrizeProductsEntity;
import com.ykm.server.entity.mng.prize.UserPrizeListEntity;
import com.ykm.server.service.PrizeFrontService;
import com.ykm.server.service.mng.prize.PrizeActivityService;
import com.ykm.server.service.mng.prize.PrizeGoodsService;
import com.ykm.server.service.mng.prize.PrizeProductsService;
import com.ykm.server.service.mng.prize.UserPrizeListService;
import com.ykm.server.utils.CookiesUtil;
import com.ykm.server.utils.DateUtils;
import com.ykm.server.utils.JedisOpsUtil;
import com.ykm.server.utils.LotterUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/27-14:33
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@RestController("/prize")
@RequestMapping("/prize")
public class PrizeAPIController {

    @Value("${prizeAccessDomain}")
    private String prizeAccessDomain;

    @Autowired
    CookiesUtil cookiesUtil;

    @Autowired
    PrizeFrontService service;

    @Autowired
    PrizeGoodsService prizeGoodsService;

    @Autowired
    PrizeActivityService prizeActivityService;

    @Autowired
    PrizeProductsService prizeProductsService;


    @Autowired
    UserPrizeListService prizeListService;

    @Autowired
    JedisOpsUtil jedisOpsUtil;

    @RequestMapping("/list")
    public ResponseDto<PrizeProductsListDto> list(Integer prizeId, Integer page, Integer pageSize, HttpServletRequest request) throws BusinessException {

        String deviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        String appKey = cookiesUtil.getValue("appkey", request.getCookies());
        checkIllegal(prizeId, appKey, deviceId);

        PrizeProductsListDto listDto = new PrizeProductsListDto();

        List<PrizeProductsEntity> list = service.list(prizeId, page, pageSize);

        if(list == null || list.size() == 0){
            return new ResponseDto<>(listDto, ResponseCode.OK);
        }

        for(PrizeProductsEntity entity : list){
            PrizeProductsDto dto = new PrizeProductsDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setHitRate(null);
            listDto.getContent().add(dto);
        }

        return new ResponseDto<>(listDto, ResponseCode.OK);
    }

    @RequestMapping("/getPrizeActivityInfo")
    public ResponseDto<PrizeActivityDto> getPrizeActivityInfo(Integer prizeId, HttpServletRequest request) throws BusinessException {

        String deviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        String appKey = cookiesUtil.getValue("appkey", request.getCookies());
        checkIllegal(prizeId, appKey, deviceId);

        PrizeActivityDto dto = new PrizeActivityDto();

        PrizeActivityEntity prizeActivityEntity = prizeActivityService.get(prizeId);

        if(prizeActivityEntity == null){
            return new ResponseDto<>(dto, ResponseCode.OK);
        }

        BeanUtils.copyProperties(prizeActivityEntity, dto);

        dto.setCount(getRemain(deviceId));

        return new ResponseDto<>(dto, ResponseCode.OK);
    }


    @RequestMapping("/getUserPrizeList")
    public ResponseDto<UserPrizeListDto> getUserPrizeList(Integer prizeId, HttpServletRequest request) throws BusinessException {

        String deviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        String appKey = cookiesUtil.getValue("appkey", request.getCookies());
        checkIllegal(prizeId, appKey, deviceId);

        UserPrizeListDto listDto = new UserPrizeListDto();
        long total = prizeListService.count(prizeId, deviceId);
        listDto.setTotal(total);

        List<UserPrizeListEntity> list = prizeListService.list(prizeId, deviceId,0, 10000);
        if(list == null || list.size() == 0 || prizeId == null){
            return new ResponseDto<>(listDto, ResponseCode.OK);
        }

        for(UserPrizeListEntity entity : list){
            UserPrizeDto dto = new UserPrizeDto();
            BeanUtils.copyProperties(entity, dto);
            if(entity.getExpireTime().getTime() < System.currentTimeMillis()){
                dto.setExipred(true);
            }
            listDto.getContent().add(dto);
        }
        return new ResponseDto<>(listDto, ResponseCode.OK);
    }

    @RequestMapping("/getPrize")
    public ResponseDto<PrizeProductsDto> getPrize(Integer prizeId, HttpServletRequest request) throws BusinessException {

        String deviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        String appKey = cookiesUtil.getValue("appkey", request.getCookies());
        checkIllegal(prizeId, appKey, deviceId);

        PrizeProductsDto dto = new PrizeProductsDto();

        List<PrizeProductsEntity> list = service.list(prizeId, 0, 15);

        if(list == null || list.size() == 0 || list.size() != 8){
            return new ResponseDto<>(dto, ResponseCode.Fail);
        }

        List<Double> rate = new ArrayList<>();
        for(PrizeProductsEntity entity : list){
            rate.add((double)entity.getHitRate());
        }
        LotterUtil ll = new LotterUtil(rate);
        int index = ll.randomColunmIndex();

        if(index<0 || index > list.size()-1){
            return new ResponseDto<>(null, ResponseCode.Fail);
        }
        PrizeProductsEntity prizeProductsEntity = list.get(index);
        //先扣再查
        int remain = getRemain(deviceId);
        if(remain <=0 ){
            return new ResponseDto<>(null, ResponseCode.PRIZE_REMAIN_NONE);
        }

        reduceCount(deviceId);
        dto.setCount(getRemain(deviceId));



        String currentValue = System.currentTimeMillis()+"";
        String key = prizeProductsEntity.getId()+"";
        String prefix = "PRIZE_PRODUCT_";
        boolean locked = jedisOpsUtil.lock(prefix, key,currentValue,2);

        if(locked){
            if(prizeProductsEntity.getProductTotal() > 1) {
                prizeProductsEntity.setProductTotal(prizeProductsEntity.getProductTotal() - 1);
                prizeProductsService.addOrSave(prizeProductsEntity);

                BeanUtils.copyProperties(prizeProductsEntity, dto);
                dto.setHitRate(null);

                jedisOpsUtil.unlock(prefix, key, currentValue, 2);

                return new ResponseDto<>(dto, ResponseCode.OK);

            }else{
                jedisOpsUtil.unlock(prefix, key, currentValue, 2);
                return new ResponseDto<>(null, ResponseCode.PRIZE_PRODUCT_STORE_NONE);
            }
        }

        return new ResponseDto<>(null, ResponseCode.PRIZE_PRODUCT_LOCK_FAIL);
    }


    @RequestMapping("/getGoods")
    public ResponseDto<PrizeGoodsDto> getGoods(Integer prizeId, Integer productId, HttpServletRequest request) throws BusinessException {

        String deviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        String appKey = cookiesUtil.getValue("appkey", request.getCookies());
        checkIllegal(prizeId, appKey, deviceId);

        PrizeGoodsDto dto = new PrizeGoodsDto();

        List<PrizeGoodsEntity> list = prizeGoodsService.list(null, null, productId, 0, 100);

        if(list == null || list.size() == 0 ){
            return new ResponseDto<>(dto, ResponseCode.Fail);
        }

        List<Double> rate = new ArrayList<>();
        for(PrizeGoodsEntity entity : list){
            rate.add((double)entity.getShowRate());
        }
        LotterUtil ll = new LotterUtil(rate);
        int index = ll.randomColunmIndex();

        if(index<0 || index > list.size()-1){
            return new ResponseDto<>(null, ResponseCode.Fail);
        }

        PrizeGoodsEntity prizeGoodsEntity = list.get(index);

        String currentValue = System.currentTimeMillis()+"";
        String key = prizeGoodsEntity.getId()+"";
        String prefix = "PRIZE_GOODS_";
        boolean locked = jedisOpsUtil.lock(prefix, key,currentValue,2);

        dto.setCount(getRemain(deviceId));

        if(locked){
            if(prizeGoodsEntity.getGoodsTotal() > 1) {
                prizeGoodsEntity.setGoodsTotal(prizeGoodsEntity.getGoodsTotal() - 1);
                prizeGoodsService.addOrSave(prizeGoodsEntity);

                BeanUtils.copyProperties(prizeGoodsEntity, dto);
                dto.setShowRate(null);


                // 加入用户奖品列表
                PrizeActivityEntity prizeActivityEntity = prizeActivityService.get(prizeId);

                UserPrizeListEntity userPrizeListEntity = new UserPrizeListEntity();
                BeanUtils.copyProperties(prizeGoodsEntity, userPrizeListEntity);
                userPrizeListEntity.setGetTime(new Date());
                userPrizeListEntity.setGoodId(prizeGoodsEntity.getId());
                userPrizeListEntity.setPrizeId(prizeActivityEntity.getId());
                userPrizeListEntity.setPrizeTitle(prizeActivityEntity.getPrizeTitle());
                userPrizeListEntity.setDeviceId(deviceId);
                prizeListService.addOrSave(userPrizeListEntity);

                jedisOpsUtil.unlock(prefix, key, currentValue, 2);

                return new ResponseDto<>(dto, ResponseCode.OK);

            }else{
                jedisOpsUtil.unlock(prefix, key, currentValue, 2);
                return new ResponseDto<>(null, ResponseCode.PRIZE_GOODS_STORE_NONE);
            }
        }
        return new ResponseDto<>(null, ResponseCode.PRIZE_GOODS_LOCK_FAIL);
    }

    @GetMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response, Integer prizeId, String deviceId, String appKey) throws BusinessException, IOException {

        String fromDeviceId = deviceId;
        if(StringUtils.isEmpty(deviceId)) {
            fromDeviceId = cookiesUtil.getValue("deviceId", request.getCookies());
        }
        if(StringUtils.isEmpty(fromDeviceId)){
            fromDeviceId = UUID.randomUUID().toString();
        }
        Cookie cookie = cookiesUtil.genCookie("deviceId", fromDeviceId);
        response.addCookie(cookie);
        String youAppKey = appKey;
        if(StringUtils.isEmpty(youAppKey)) {
            youAppKey = cookiesUtil.getValue("appKey", request.getCookies());
        }

        checkIllegal(prizeId, youAppKey, fromDeviceId);


        response.sendRedirect("http://"+prizeAccessDomain+"/prize/index.html?prizeId="+prizeId+"&deviceId="+fromDeviceId+"&appKey="+appKey);
    }


    private void checkIllegal(Integer prizeId, String appKey, String deviceId) throws BusinessException {
        if(StringUtils.isEmpty(deviceId)) {
            throw new BusinessException(MessageCode.PRIZE_ILLEGAL_2);
        }

        PrizeActivityEntity prizeActivityEntity = prizeActivityService.get(prizeId);
        if(prizeActivityEntity == null || prizeActivityEntity.getStatus() != 1){
            throw new BusinessException(MessageCode.PRIZE_ILLEGAL_0);
        }

        String youAppKey = appKey;
        String rightAppKey = prizeActivityEntity.getAppKey();

        if(StringUtils.isEmpty(youAppKey) || StringUtils.isEmpty(rightAppKey) || !rightAppKey.equalsIgnoreCase(youAppKey)){
            throw new BusinessException(MessageCode.PRIZE_ILLEGAL_1);
        }
    }

    private int getRemain(String deviceId){
        String countKey = deviceId+"_"+DateUtils.toYmdDate();
        String count = jedisOpsUtil.hget(2, "PRIZE_COUNT", countKey,String.class);
        if(StringUtils.isEmpty(count)){
            String defaultCount = jedisOpsUtil.hget(2, "PRIZE_COUNT", "DEFAULT",String.class);
            count = defaultCount == null ? "10" : defaultCount;
            jedisOpsUtil.hset(2, "PRIZE_COUNT", countKey, ""+count);
        }
        return Integer.parseInt(count);
    }

    private void reduceCount(String deviceId){
        String countKey = deviceId+"_"+DateUtils.toYmdDate();
        String count = jedisOpsUtil.hget(2, "PRIZE_COUNT", countKey,String.class);
        int remain = Integer.parseInt(count) - 1;
        String value = String.valueOf(remain<0?0:remain);
        jedisOpsUtil.hset(2, "PRIZE_COUNT", countKey, value);
    }
}
