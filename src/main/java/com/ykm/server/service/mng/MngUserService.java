package com.ykm.server.service.mng;

import com.ykm.server.daos.mng.MngUserDao;
import com.ykm.server.entity.mng.MngUser;
import com.ykm.server.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/8/5-15:13
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Service
public class MngUserService {
    @Autowired
    MngUserDao mngUserDao;

    public MngUser getById(Integer id){
        if(id == null) return null;
        Optional<MngUser> optional =  mngUserDao.findById(id);
        if(optional.isPresent()) return optional.get();
        return null;
    }

    public MngUser findByAccountAndPasswd(String account, String passwd){
        if(StringUtils.isEmpty(account) || StringUtils.isEmpty(passwd)) return null;
        passwd = MD5Util.md5(passwd);
        MngUser mngUser =  mngUserDao.findByAccountAndPasswd(account, passwd);
        return mngUser;
    }
}
