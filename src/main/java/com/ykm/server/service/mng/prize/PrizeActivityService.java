package com.ykm.server.service.mng.prize;

import com.ykm.server.daos.mng.prize.PrizeActivityDao;
import com.ykm.server.entity.mng.prize.PrizeActivityEntity;
import com.ykm.server.utils.MD5Util;
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
public class PrizeActivityService {

    @Autowired
    PrizeActivityDao dao;

    public PrizeActivityEntity get(Integer id) throws Exception {
        Optional<PrizeActivityEntity> subjectOptional = dao.findById(id);
        if(!subjectOptional.isPresent()){
            return null;
        }
        return subjectOptional.get();
    }


    public boolean addOrSave(PrizeActivityEntity object){
        if(object == null) return false;
        if(object.getId() != null) {
            object.setUpdateTime(new Date());
        }else{
            object.setCreateTime(new Date());
            object.setUpdateTime(new Date());
            object.setAppKey(MD5Util.md5(System.currentTimeMillis()+""));
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

    public long count(Integer id, String prizeTitle, Integer status){
        return dao.count(specification(id ,prizeTitle, status));
    }

    public List<PrizeActivityEntity> list(Integer id, String prizeTitle, Integer status, Integer page, Integer pageSize){
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 15: pageSize;

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);

        Page<PrizeActivityEntity> pageResult = dao.findAll(
                specification(id, prizeTitle, status),
                pageable);

        if(pageResult.hasContent()){
            return pageResult.getContent();
        }

        return null;
    }


    private Specification specification(Integer id, String prizeTitle, Integer status) {
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtils.isEmpty(prizeTitle)){
                list.add(criteriaBuilder.like(root.get("prizeTitle"), "%"+prizeTitle+"%"));
            }
            if(status!=null){
                list.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if(id!=null){
                list.add(criteriaBuilder.equal(root.get("id"), id));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return specification;
    }
}
