package com.ykm.server.service.mng.prize;

import com.ykm.server.daos.mng.prize.UserPrizeListDao;
import com.ykm.server.entity.mng.prize.UserPrizeListEntity;
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
public class UserPrizeListService {

    @Autowired
    UserPrizeListDao dao;

    public UserPrizeListEntity get(Integer id){
        Optional<UserPrizeListEntity> subjectOptional = dao.findById(id);
        if(!subjectOptional.isPresent()){
            return null;
        }
        return subjectOptional.get();
    }

    public boolean addOrSave(UserPrizeListEntity object){
        if(object == null) return false;
        object = dao.save(object);
        return object != null;
    }

    public boolean delete(Integer id){
        if(id==null) return false ;
        dao.deleteById(id);
        //级联删除关联表
        return true;
    }

    public long count(Integer prizeId , String deviceId){
        return dao.count(specification(prizeId, deviceId));
    }

    public List<UserPrizeListEntity> list(Integer prizeId, String deviceId, Integer page, Integer pageSize){
        page = (page == null || page == 0) ? 1 : page;
        pageSize = (pageSize == null || pageSize ==0)? 15: pageSize;

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, pageSize, sort);

        Page<UserPrizeListEntity> pageResult = dao.findAll(
                specification(prizeId, deviceId),
                pageable);

        if(pageResult.hasContent()){
            return pageResult.getContent();
        }

        return null;
    }


    private Specification specification(Integer prizeId, String deviceId) {
        Specification specification = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtils.isEmpty(deviceId)){
                list.add(criteriaBuilder.equal(root.get("deviceId"), deviceId));
            }
            if(prizeId != null){
                list.add(criteriaBuilder.equal(root.get("prizeId"), prizeId));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return specification;
    }
}
