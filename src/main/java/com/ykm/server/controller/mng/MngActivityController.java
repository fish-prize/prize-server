package com.ykm.server.controller.mng;

import com.ykm.server.common.Authority;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import com.ykm.server.controller.mng.dto.PrizeActivityDto;
import com.ykm.server.controller.mng.dto.PrizeActivityListDto;
import com.ykm.server.entity.mng.prize.PrizeActivityEntity;
import com.ykm.server.service.mng.prize.PrizeActivityService;
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
@RestController("/mng/activity")
@RequestMapping("/mng/activity")
public class MngActivityController {

    @Autowired
    private PrizeActivityService service;

    @RequestMapping("/list")
    public ResponseDto<PrizeActivityListDto> list(Integer id, String prizeTitle, Integer status, Integer page, Integer pageSize) {
        PrizeActivityListDto listDto = new PrizeActivityListDto();
        long total = service.count(id, prizeTitle, status);
        listDto.setTotal(total);

        List<PrizeActivityEntity> list = service.list(id, prizeTitle, status, page, pageSize);
        if(list == null || list.size() == 0){
            return new ResponseDto<>(listDto, ResponseCode.OK);
        }

        for(PrizeActivityEntity entity : list){
            PrizeActivityDto dto = new PrizeActivityDto();
            BeanUtils.copyProperties(entity, dto);
            listDto.getContent().add(dto);
        }
        return new ResponseDto<>(listDto, ResponseCode.OK);
    }

    @RequestMapping("/update")
    public ResponseDto<Boolean> update(@RequestBody PrizeActivityDto dto){
        if(dto == null){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        PrizeActivityEntity entity = new PrizeActivityEntity();
        BeanUtils.copyProperties(dto, entity);
        boolean result = service.addOrSave(entity);
        return new ResponseDto<>(result, ResponseCode.OK);
    }

    @RequestMapping("/delete")
    public ResponseDto<Boolean> delete(@RequestBody PrizeActivityDto dto){
        if(dto == null || dto.getId() == null ){
            return new ResponseDto<>(false, ResponseCode.Fail);
        }
        boolean result = service.delete(dto.getId());
        return new ResponseDto<>(result, ResponseCode.OK);
    }
}


