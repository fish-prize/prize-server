package com.ykm.server.controller.mng;

import com.ykm.server.common.Authority;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.PrizeProductsDto;
import com.ykm.server.controller.mng.dto.PrizeProductsListDto;
import com.ykm.server.entity.mng.prize.PrizeProductsEntity;
import com.ykm.server.service.mng.prize.PrizeProductsService;
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
@RestController("/mng/products")
@RequestMapping("/mng/products")
public class MngProductsController {

    @Autowired
    private PrizeProductsService service;

    @RequestMapping("/list")
    public ResponseDto<PrizeProductsListDto> list(Integer id, String productName, Integer prizeId, Integer page, Integer pageSize) {
        PrizeProductsListDto listDto = new PrizeProductsListDto();
        long total = service.count(id, productName, prizeId);
        listDto.setTotal(total);

        List<PrizeProductsEntity> list = service.list(id, productName, prizeId, page, pageSize);
        if(list == null || list.size() == 0){
            return new ResponseDto<>(listDto, ResponseCode.OK);
        }

        for(PrizeProductsEntity entity : list){
            PrizeProductsDto dto = new PrizeProductsDto();
            BeanUtils.copyProperties(entity, dto);
            listDto.getContent().add(dto);
        }
        return new ResponseDto<>(listDto, ResponseCode.OK);
    }

    @RequestMapping("/update")
    public ResponseDto<Boolean> update(@RequestBody PrizeProductsDto dto){
        if(dto == null){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        PrizeProductsEntity entity = new PrizeProductsEntity();
        BeanUtils.copyProperties(dto, entity);
        boolean result = service.addOrSave(entity);
        return new ResponseDto<>(result, ResponseCode.OK);
    }

    @RequestMapping("/delete")
    public ResponseDto<Boolean> delete(@RequestBody PrizeProductsDto dto){
        if(dto == null || dto.getId() == null ){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        boolean result = service.delete(dto.getId());
        return new ResponseDto<>(result, ResponseCode.OK);
    }
}


