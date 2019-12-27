package com.ykm.server.daos.mng.prize;

import com.ykm.server.entity.mng.prize.PrizeProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/24-15:37
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Repository
public interface PrizeProductsDao extends JpaRepository<PrizeProductsEntity,Integer>, JpaSpecificationExecutor<PrizeProductsEntity> {

}
