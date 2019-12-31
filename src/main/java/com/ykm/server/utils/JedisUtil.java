package com.ykm.server.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class JedisUtil {

    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);
    private static Map<String, JedisPool> pools = new HashMap<>();

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.auth}")
    private String auth;

    public Jedis getJedis(int db){
        try {
            String key = "db_" + db;
            if (pools.containsKey(key)) {
                return pools.get(key).getResource();
            }

            final HostAndPort hnp = new HostAndPort(host, port <= 0 ? Protocol.DEFAULT_PORT : port);

            log.info("The redis server is host = {},port = {},auth = {},db = {}", host, port, auth, db);
            JedisPoolConfig poolConfig = new JedisPoolConfig();

            poolConfig.setMaxIdle(5);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setMaxWaitMillis(10000);
            poolConfig.setMaxWaitMillis(60 * 1000);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMaxTotal(100);

            JedisPool pool = null;
            if(StringUtils.isEmpty(auth)){
                pool = new JedisPool(poolConfig, hnp.getHost(), hnp.getPort(), 10000, null, db);
            }else {
                pool = new JedisPool(poolConfig, hnp.getHost(), hnp.getPort(), 10000, auth, db);
            }
            pools.put(key, pool);

            return pool.getResource();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
