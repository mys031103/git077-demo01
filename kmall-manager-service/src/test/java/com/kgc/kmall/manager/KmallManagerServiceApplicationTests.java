package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.manager.utils.RedisUtil;
import com.kgc.kmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {
@Resource
    RedisUtil redisUtil;

@Test
    void test01(){
    /*try {
        Jedis jedis = redisUtil.getJedis();
        String name = jedis.get("name");
        System.out.println(name);
    }catch (JedisConnectionException e){
        e.printStackTrace();
    }*/
    Jedis jedis=redisUtil.getJedis();
    String ping = jedis.ping();
    System.out.println(ping);
}
}
