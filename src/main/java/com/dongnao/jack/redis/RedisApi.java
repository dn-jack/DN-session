package com.dongnao.jack.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisApi {
    
    private static JedisPool pool;
    
    private static Properties prop = null;
    
    static {
        InputStream in = RedisApi.class.getClassLoader()
                .getResourceAsStream("com/dongnao/jack/redis/redis.properties");
        
        prop = new Properties();
        try {
            prop.load(in);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        JedisPoolConfig config = new JedisPoolConfig();
        // 鎺у埗涓�釜pool鍙垎閰嶅灏戜釜jedis瀹炰緥锛岄�杩噋ool.getResource()鏉ヨ幏鍙栵紱  
        // 濡傛灉璧嬪�涓�1锛屽垯琛ㄧず涓嶉檺鍒讹紱濡傛灉pool宸茬粡鍒嗛厤浜唌axActive涓猨edis瀹炰緥锛屽垯姝ゆ椂pool鐨勭姸鎬佷负exhausted(鑰楀�?銆� 
        config.setMaxTotal(Integer.valueOf(prop.getProperty("MAX_TOTAL")));
        //鎺у埗涓�釜pool鏈�鏈夊灏戜釜鐘舵�涓篿dle(绌洪棽鐨�鐨刯edis瀹炰緥銆�?
        config.setMaxIdle(Integer.valueOf(prop.getProperty("MAX_IDLE")));
        
        // 琛ㄧず褰揵orrow(寮曞�?涓�釜jedis瀹炰緥鏃讹紝鏈�ぇ鐨勭瓑寰呮椂闂达紝濡傛灉瓒呰繃绛夊緟鏃堕棿锛屽垯鐩存帴鎶涘嚭JedisConnectionException锛� 
        config.setMaxWaitMillis(Integer.valueOf(prop.getProperty("MAX_WAIT_MILLIS")));
        
        //鍦╞orrow涓�釜jedis瀹炰緥鏃讹紝鏄惁鎻愬墠杩涜validate鎿嶄綔锛涘鏋滀负true锛屽垯寰�?��鐨刯edis瀹炰緥鍧囨槸鍙敤鐨勶紱  
        config.setTestOnBorrow(Boolean.valueOf(prop.getProperty("TEST_ON_BORROW")));
        //鍦ㄨ繘琛宺eturnObject瀵硅繑鍥炵殑connection杩涜validateObject鏍￠�?
        config.setTestOnReturn(Boolean.valueOf(prop.getProperty("TEST_ON_RETURN")));
        //瀹氭椂�?圭嚎绋嬫睜涓┖闂茬殑閾炬帴杩涜validateObject鏍￠�?
        config.setTestWhileIdle(Boolean.valueOf(prop.getProperty("TEST_WHILE_IDLE")));
        pool = new JedisPool(config, prop.getProperty("REDIS_IP"),
                Integer.valueOf(prop.getProperty("REDIS_PORT")), 100000);
    }
    
    /**
     * 鏋勫缓redis杩炴帴姹�?
     */
    public static JedisPool getPool() {
        
        if (pool == null) {
            
            JedisPoolConfig config = new JedisPoolConfig();
            // 鎺у埗涓�釜pool鍙垎閰嶅灏戜釜jedis瀹炰緥锛岄�杩噋ool.getResource()鏉ヨ幏鍙栵紱  
            // 濡傛灉璧嬪�涓�1锛屽垯琛ㄧず涓嶉檺鍒讹紱濡傛灉pool宸茬粡鍒嗛厤浜唌axActive涓猨edis瀹炰緥锛屽垯姝ゆ椂pool鐨勭姸鎬佷负exhausted(鑰楀�?銆� 
            config.setMaxTotal(Integer.valueOf(prop.getProperty("MAX_TOTAL")));
            //鎺у埗涓�釜pool鏈�鏈夊灏戜釜鐘舵�涓篿dle(绌洪棽鐨�鐨刯edis瀹炰緥銆�?
            config.setMaxIdle(Integer.valueOf(prop.getProperty("MAX_IDLE")));
            
            // 琛ㄧず褰揵orrow(寮曞�?涓�釜jedis瀹炰緥鏃讹紝鏈�ぇ鐨勭瓑寰呮椂闂达紝濡傛灉瓒呰繃绛夊緟鏃堕棿锛屽垯鐩存帴鎶涘嚭JedisConnectionException锛� 
            config.setMaxWaitMillis(Integer.valueOf(prop.getProperty("MAX_WAIT_MILLIS")));
            
            //鍦╞orrow涓�釜jedis瀹炰緥鏃讹紝鏄惁鎻愬墠杩涜validate鎿嶄綔锛涘鏋滀负true锛屽垯寰�?��鐨刯edis瀹炰緥鍧囨槸鍙敤鐨勶紱  
            config.setTestOnBorrow(Boolean.valueOf(prop.getProperty("TEST_ON_BORROW")));
            //鍦ㄨ繘琛宺eturnObject瀵硅繑鍥炵殑connection杩涜validateObject鏍￠�?
            config.setTestOnReturn(Boolean.valueOf(prop.getProperty("TEST_ON_RETURN")));
            //瀹氭椂�?圭嚎绋嬫睜涓┖闂茬殑閾炬帴杩涜validateObject鏍￠�?
            config.setTestWhileIdle(Boolean.valueOf(prop.getProperty("TEST_WHILE_IDLE")));
            pool = new JedisPool(config, prop.getProperty("REDIS_IP"),
                    Integer.valueOf(prop.getProperty("REDIS_PORT")));
        }
        
        return pool;
    }
    
    public static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            pool.returnResource(redis);
        }
    }
    
    public static Long hdel(String key, String key1) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, key1);
        }
        catch (Exception e) {
            
        }
        finally {
            returnResource(pool, jedis);
        }
        return null;
    }
    
    /** 
     * 鑾峰彇鏁版嵁 
     *  
     * @param key 
     * @return 
     */
    public static String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        }
        catch (Exception e) {
            
        }
        finally {
            returnResource(pool, jedis);
        }
        return value;
    }
    
    /**
     * set鏁版�?
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        }
        catch (Exception e) {
            
            return "0";
        }
        finally {
            returnResource(pool, jedis);
        }
    }
    
    public static Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(key);
        }
        catch (Exception e) {
            return null;
        }
        finally {
            returnResource(pool, jedis);
        }
    }
    
    public static Long lpush(String key, String[] strings) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, strings);
        }
        catch (Exception e) {
            
            return 0L;
        }
        finally {
            returnResource(pool, jedis);
        }
    }
    
    public static List<String> lrange(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, 0, -1);
        }
        catch (Exception e) {
            return null;
        }
        finally {
            returnResource(pool, jedis);
        }
    }
    
    public static String hmset(String key, Map map) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmset(key, map);
        }
        catch (Exception e) {
            
            return "0";
        }
        finally {
            returnResource(pool, jedis);
        }
    }
    
    public static List<String> hmget(String key, String... strings) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            return jedis.hmget(key, strings);
        }
        catch (Exception e) {
            
        }
        finally {
            returnResource(pool, jedis);
        }
        return null;
    }
}
