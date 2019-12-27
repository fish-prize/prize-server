package com.ykm.server.daos.mng;

import com.ykm.server.entity.mng.MngUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/8/5-15:02
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Repository
public interface MngUserDao extends JpaRepository<MngUser, Integer> {
    MngUser findByAccountAndPasswd(String account, String passwd);
}
