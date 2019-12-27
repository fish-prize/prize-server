package com.ykm.server.service;

import com.ykm.server.daos.mng.prize.PrizeProductsDao;
import com.ykm.server.entity.mng.prize.PrizeProductsEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * wenxy
 * 功能：前端接口服务类
 * 日期：2019/12/24-15:38
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Service
public class PrizeFrontService {

    @Autowired
    PrizeProductsDao dao;

    public List<PrizeProductsEntity> list(Integer prizeId, Integer page, Integer pageSize){
        page = (page == null || page ==0) ? 1 : page;
        pageSize = (pageSize == null || pageSize== 0) ? 15 : pageSize;
        prizeId = prizeId == null ? 0 :prizeId;

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);

        Page<PrizeProductsEntity> pageResult = dao.findAll(
                specification(prizeId),
                pageable);

        if(pageResult.hasContent()){
            return pageResult.getContent();
        }
        return null;
    }


    public PrizeProductsEntity random(Integer prizeId){
        return null;
    }

    private Specification specification(Integer prizeId) {
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(prizeId!=null){
                list.add(criteriaBuilder.equal(root.get("prizeId"), prizeId));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return specification;
    }


}
