package com.ykm.server.controller.prize;

import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.*;
import com.ykm.server.entity.mng.prize.PrizeActivityEntity;
import com.ykm.server.entity.mng.prize.PrizeGoodsEntity;
import com.ykm.server.entity.mng.prize.PrizeProductsEntity;
import com.ykm.server.service.PrizeFrontService;
import com.ykm.server.service.mng.prize.PrizeGoodsService;
import com.ykm.server.utils.LotterUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Autowired
    PrizeFrontService service;

    @Autowired
    PrizeGoodsService prizeGoodsService;

    @RequestMapping("/list")
    public ResponseDto<PrizeProductsListDto> list(Integer prizeId, Integer page, Integer pageSize) {
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


    @RequestMapping("/getPrize")
    public ResponseDto<PrizeProductsDto> getPrize(Integer prizeId) {
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

        BeanUtils.copyProperties(list.get(index), dto);
        dto.setHitRate(null);

        return new ResponseDto<>(dto, ResponseCode.OK);
    }


    @RequestMapping("/getGoods")
    public ResponseDto<PrizeGoodsDto> getGoods(Integer productId) {
        PrizeGoodsDto dto = new PrizeGoodsDto();

        List<PrizeGoodsEntity> list = prizeGoodsService.list(null, null, productId, 0, 100);

        if(list == null || list.size() == 0 || list.size() != 8){
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

        BeanUtils.copyProperties(list.get(index), dto);
        dto.setShowRate(null);

        return new ResponseDto<>(dto, ResponseCode.OK);
    }

}
