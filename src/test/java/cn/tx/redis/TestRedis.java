package cn.tx.redis;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestRedis {

    @Test
    public void Test(){

        Jedis jedis=new Jedis("192.168.160.134",6379);
        //user 数据很大，查询很频繁，需要把user的数据存到缓存中

        //要想实现业务 类似查询数据库select * from user where age=201
        //在插入的时候 判断age=201 然后设置AGE_201
        //jedis.sadd("AGE_201",user1) 只有user1 age=201
        // Set<String> age_201 = jedis.smembers("AGE_201");
        //意思就是把数据分类 然后 根据固定的key 获取数据
        //select * from user where age=201 and sex="男"
        //jedis.sinter("AGE_201","SEX_男");


        User user1 = new User(UUID.randomUUID().toString(), "zjh1", 201, "男");
        User user2 = new User(UUID.randomUUID().toString(), "zjh2", 202, "男");
        User user3 = new User(UUID.randomUUID().toString(), "zjh3", 203, "女");
        User user4 = new User(UUID.randomUUID().toString(), "zjh4", 204, "男");
        User user5 = new User(UUID.randomUUID().toString(), "zjh5", 205, "女");

        Map<String,String> map = new HashMap<String,String>();

        map.put("user1", JSON.toJSONString(user1));
        map.put("user2", JSON.toJSONString(user2));
        map.put("user3", JSON.toJSONString(user3));
        map.put("user4", JSON.toJSONString(user4));
        map.put("user5", JSON.toJSONString(user5));

        jedis.hmset("SYS_USER_TABLE", map);

        System.out.println(jedis.hkeys("map"));
        System.out.println(jedis.hvals("map"));

        //  jedis.flushAll();

    }

}