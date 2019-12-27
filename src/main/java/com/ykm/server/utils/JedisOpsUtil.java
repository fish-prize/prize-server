package com.ykm.server.utils;

import com.google.gson.Gson;
import com.ykm.server.consts.JedisConsts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/3/7-14:17
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Component
public class JedisOpsUtil {

    private static final Logger log = LoggerFactory.getLogger(JedisOpsUtil.class);
    @Autowired
    JedisUtil JedisUtil;

    private static Gson gson =  new Gson();
    /**
     * 从Hash中获取field对应的值
     * @param key
     * @param field
     * @return
     */
    public  <T> T hget(int db,String key,String field,Class<T> tClass){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            String value = jedis.hget(key,field);
            return  transfer(value,tClass);
        }finally {
           if(null !=jedis ) {
               jedis.close();
           }
        }
    }

    public <T> List<T> hmget(int db,String key,Class<T> tClass,String... fields){
        Jedis jedis = null;
        try{
            List<T> rtn = new ArrayList<>();
            jedis = JedisUtil.getJedis(db);
            List<String> result =  jedis.hmget(key,fields);
            for(String tmp : result){
                T t = transfer(tmp,tClass);
                rtn.add(t);
            }
            return rtn;
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public  long exp(int db,String key,int seconds){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return jedis.expire(key,seconds);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public long incr(int db, String key){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return jedis.incr(key);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public long decr(int db, String key){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return jedis.decr(key);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public Set<String> hkeys(int db, String key){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return jedis.hkeys(key);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }
    public long hdel(int db, String key, String... fields){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return  jedis.hdel(key,fields);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public long hset(int db, String key, String field, String value){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return  jedis.hset(key,field,value);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }


    public  boolean sismember(int db,String key,String member){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            boolean value = jedis.sismember(key,member);
            return value;
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    public  long sadd(int db,String key,String... members){
        Jedis jedis = null;
        try{
            jedis = JedisUtil.getJedis(db);
            return jedis.sadd(key,members);
        }finally {
            if(null !=jedis ) {
                jedis.close();
            }
        }
    }

    /**
     * set
     * @param db
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public String set(int db, String key, String value, JedisConsts.SetParams nxxx, JedisConsts.SetParams expx, int expire) {
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis(db);
            return jedis.set(key, value, nxxx.getParam(), expx.getParam(), expire);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * get
     * @param db
     * @param key
     * @return
     */
    public String get(int db, String key) {
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis(db);
            return jedis.get(key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * del
     * @param db
     * @param key
     * @return
     */
    public Long del(int db, String key) {
        Jedis jedis = null;
        try {
            jedis = JedisUtil.getJedis(db);
            return jedis.del(key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    private <T> T transfer(String value,Class<T> t){

        try {

            if (StringUtils.isEmpty(value) && t.getName().equals("String")) {
                return gson.fromJson("", t);
            }

            if (StringUtils.isEmpty(value) && !t.getName().equals("String")) {
                return gson.fromJson("0", t);
            }

            //处理格式[1,0]的value
            boolean match = Pattern.matches("^\\[\\S+,\\S+\\]$", value);
            if (match) {
                value = value.replaceAll("^\\[\\S+,", "");
                value = value.replaceAll("\\]$", "");
                return gson.fromJson(value, t);
            }

            return gson.fromJson(value, t);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

        return null;
    }

    public static void main(String[] args){
        JedisOpsUtil jedisOpsUtil = new JedisOpsUtil();
        Integer value = jedisOpsUtil.transfer("[0,10]",Integer.class);
        System.out.println(value);
    }
}
