package com.ykm.server.controller.mng;

import com.ykm.server.common.Authority;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.PrizeGoodsDto;
import com.ykm.server.controller.mng.dto.PrizeGoodsListDto;
import com.ykm.server.entity.mng.prize.PrizeGoodsEntity;
import com.ykm.server.service.mng.prize.PrizeGoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 有限公司
 * 功能：
 * 日期：2019/12/24-13:47
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Authority(value = "mng")
@RestController("/mng/goods")
@RequestMapping("/mng/goods")
public class MngGoodsController {

    @Autowired
    private PrizeGoodsService service;

    @RequestMapping("/list")
    public ResponseDto<PrizeGoodsListDto> list(Integer id, String goodsName, Integer productId, Integer page, Integer pageSize) {
        PrizeGoodsListDto listDto = new PrizeGoodsListDto();
        long total = service.count(id, goodsName, productId);
        listDto.setTotal(total);

        List<PrizeGoodsEntity> list = service.list(id, goodsName, productId, page, pageSize);
        if(list == null || list.size() == 0){
            return new ResponseDto<>(listDto, ResponseCode.OK);
        }

        for(PrizeGoodsEntity entity : list){
            PrizeGoodsDto dto = new PrizeGoodsDto();
            BeanUtils.copyProperties(entity, dto);
            listDto.getContent().add(dto);
        }
        return new ResponseDto<>(listDto, ResponseCode.OK);
    }

    @RequestMapping("/update")
    public ResponseDto<Boolean> update(@RequestBody PrizeGoodsDto dto){
        if(dto == null){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        PrizeGoodsEntity entity = new PrizeGoodsEntity();
        BeanUtils.copyProperties(dto, entity);
        boolean result = service.addOrSave(entity);
        return new ResponseDto<>(result, ResponseCode.OK);
    }

    @RequestMapping("/delete")
    public ResponseDto<Boolean> delete(@RequestBody PrizeGoodsDto dto){
        if(dto == null || dto.getId() == null ){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        boolean result = service.delete(dto.getId());
        return new ResponseDto<>(result, ResponseCode.OK);
    }
}


