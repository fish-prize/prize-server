package com.ykm.server.service.mng.prize;

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
 * 功能：
 * 日期：2019/12/24-15:38
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Service
public class PrizeProductsService {

    @Autowired
    PrizeProductsDao dao;

    public PrizeProductsEntity get(Integer id) throws Exception {
        Optional<PrizeProductsEntity> subjectOptional = dao.findById(id);
        if(!subjectOptional.isPresent()){
            return null;
        }
        return subjectOptional.get();
    }

    public boolean addOrSave(PrizeProductsEntity object){
        if(object == null) return false;
        if(object.getId() != null) {
            object.setUpdateTime(new Date());
        }else{
            object.setCreateTime(new Date());
            object.setUpdateTime(new Date());
        }
        object = dao.save(object);
        return object != null;
    }

    public boolean delete(Integer id){
        if(id==null) return false ;
        dao.deleteById(id);
        //级联删除关联表
        return true;
    }

    public long count(Integer id, String productName, Integer prizeId){
        return dao.count(specification(id ,productName, prizeId));
    }

    public List<PrizeProductsEntity> list(Integer id, String productName, Integer prizeId, Integer page, Integer pageSize){
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 15: pageSize;

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);

        Page<PrizeProductsEntity> pageResult = dao.findAll(
                specification(id, productName, prizeId),
                pageable);

        if(pageResult.hasContent()){
            return pageResult.getContent();
        }

        return null;
    }


    private Specification specification(Integer id, String productName, Integer prizeId) {
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtils.isEmpty(productName)){
                list.add(criteriaBuilder.like(root.get("productName"), "%"+productName+"%"));
            }
            if(prizeId!=null){
                list.add(criteriaBuilder.equal(root.get("prizeId"), prizeId));
            }
            if(id!=null){
                list.add(criteriaBuilder.equal(root.get("id"), id));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return specification;
    }
}
