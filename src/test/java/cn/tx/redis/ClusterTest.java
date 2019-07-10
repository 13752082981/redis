package cn.tx.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterTest {

    public static JedisCluster jedisCluster;

    public void set(String key, String value) {
        jedisCluster.set(key, value);
    }

    public static void main(String[] args) {

        ApplicationContext ac =  new ClassPathXmlApplicationContext("classpath:/applicationContext-cluster.xml");
        jedisCluster = (JedisCluster)ac.getBean("redisCluster");

       for (int i=0; i<100; i++) {
        	jedisCluster.set("name" + i, "value" + i);
        }

        System.out.println(jedisCluster.get("name4"));
    }
}