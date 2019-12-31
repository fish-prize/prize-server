package com.ykm.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/29-11:46
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class DateUtils {

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    public static String toYmdDate(){
        return  sf.format(new Date());
    }

    public static void main(String[] args){
        System.out.println(toYmdDate());
    }

}
