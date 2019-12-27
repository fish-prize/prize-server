package com.ykm.server.service.mng.prize;

import com.ykm.server.daos.mng.prize.PrizeGoodsDao;
import com.ykm.server.entity.mng.prize.PrizeGoodsEntity;
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
public class PrizeGoodsService {

    @Autowired
    PrizeGoodsDao dao;

    public PrizeGoodsEntity get(Integer id) throws Exception {
        Optional<PrizeGoodsEntity> subjectOptional = dao.findById(id);
        if(!subjectOptional.isPresent()){
            return null;
        }
        return subjectOptional.get();
    }

    public boolean addOrSave(PrizeGoodsEntity object){
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

    public long count(Integer id, String goodsName, Integer productId){
        return dao.count(specification(id ,goodsName, productId));
    }

    public List<PrizeGoodsEntity> list(Integer id, String goodsName, Integer productId, Integer page, Integer pageSize){
        page = (page == null || page == 0) ? 1 : page;
        pageSize = ( pageSize == null || pageSize == 0) ? 15: pageSize;

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);

        Page<PrizeGoodsEntity> pageResult = dao.findAll(
                specification(id, goodsName, productId),
                pageable);

        if(pageResult.hasContent()){
            return pageResult.getContent();
        }

        return null;
    }

    private Specification specification(Integer id, String goodsName, Integer productId) {
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtils.isEmpty(goodsName)){
                list.add(criteriaBuilder.like(root.get("goodsName"), "%"+goodsName+"%"));
            }
            if(productId!=null){
                list.add(criteriaBuilder.equal(root.get("productId"), productId));
            }
            if(id!=null){
                list.add(criteriaBuilder.equal(root.get("id"), id));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return specification;
    }
}
