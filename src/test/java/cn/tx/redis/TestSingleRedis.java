package cn.tx.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.*;

import java.util.*;

public class TestSingleRedis {
    //单独连接一台服务器
    private static Jedis jedis;
    //主从 或者哨兵
    private static ShardedJedis shard;
    //连接池
    private static ShardedJedisPool pool;


    @BeforeClass
    public static void before(){
        jedis=new Jedis("192.168.160.134",6379);

        //分片
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("192.168.160.134",6379));

        shard=new ShardedJedis(shards);
        GenericObjectPoolConfig config= new GenericObjectPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(20);
        config.setMaxWaitMillis(-1);
        config.setTestOnBorrow(true);

        pool=new ShardedJedisPool(config, shards);

    }

    @AfterClass
    public static  void after(){
        jedis.disconnect();
        shard.disconnect();
        pool.destroy();
    }
    @Test
    public void TestString(){
        jedis.set("name", "zjh");
        System.out.println(jedis.get("name"));

        jedis.append("name", "is my love");//拼接
        System.out.println(jedis.get("name"));

        jedis.del("name");
        System.out.println(jedis.get("name"));

        //设置多个键值对
        jedis.mset("name","zjh","age","27");
        jedis.incr("age");//递增
        System.out.println(jedis.get("name")+"=="+jedis.get("age"));

    }
    @Test
    public void TestMap(){
        //添加数据
        Map<String,String> map =new HashMap<String,String>();
        map.put("name", "zjh");
        map.put("age", "20");
        jedis.hmset("user",map);
        //去除user的name,执行结果【minxr】结果是一个泛型的LIST
        //第一个参数是存入redis中的map对象的key,后面跟着的是放入map中的key
        List<String> hmget = jedis.hmget("user", "name", "age");
        System.out.println(hmget);

        //删除map中的某个键
        jedis.hdel("user", "age");
        System.out.println(jedis.hmget("user","age"));//删除了返回null
        System.out.println(jedis.hlen("user"));//返回key=user中的存放的值为1
        // jedis.flushAll()//删除所有数据
        System.out.println(jedis.exists("user"));//是否存在key=user 返回true
        System.out.println(jedis.hkeys("user"));//返回user中的key
        System.out.println(jedis.hvals("user"));//返回user中的value


        Set<String> hkeys = jedis.hkeys("user");

        for (Iterator<String> hkey = hkeys.iterator(); hkey.hasNext(); ) {
            System.out.println("11111111");
            String key =  hkey.next();
            System.out.println("key=="+key+"----  value=="+jedis.hmget("user",key));
        }
    }

    @Test
    public void TestList(){
        //开始前 删除所有的数据
        jedis.flushAll();

        jedis.lpush("name", "zjh1");
        jedis.lpush("name", "zjh2");
        jedis.lpush("name", "zjh3");

        //取出所有的数据 jedis
        //第一个参数：是key 第二个是起始位置 第三个是表示取所有
        System.out.println(jedis.lrange("name", 0, -1));

        jedis.del("name");
        jedis.rpush("name", "zjh1");
        jedis.rpush("name", "zjh2");
        jedis.rpush("name", "zjh3");

        System.out.println(jedis.lrange("name", 0, -1));

    }
    @Test
    public void TestSet(){
        //开始前 删除所有的数据
        jedis.flushAll();

        jedis.sadd("name", "zjh1");
        jedis.sadd("name", "zjh2");
        jedis.sadd("name", "zjh3");

        //移除 name=zjh3
        jedis.srem("name", "zjh3");

        System.out.println(jedis.smembers("name"));//获取所有加入的value
        System.out.println(jedis.sismember("name","zjh2"));//判断zjh2 是否在name集合里面
        System.out.println(jedis.srandmember("name"));//随机返回一个元素值
        System.out.println(jedis.scard("name"));//返回集合的元素个数

    }
    @Test
    public void TestRLPush(){
        jedis.del("name");
        //排序
        //此处的lpush 和rpush 是LIST的操作，是一个双向链表
        jedis.lpush("name", "1");
        jedis.lpush("name", "2");
        jedis.lpush("name", "5");
        jedis.lpush("name", "6");
        jedis.lpush("name", "4");
        System.out.println(jedis.lrange("name", 0, -1));//46521
        System.out.println(jedis.sort("name"));//12345

    }

    @Test
    public void TestTrans(){
        jedis.flushAll();
        long start = System.currentTimeMillis();
        Transaction tx = jedis.multi();

        for (int i = 0; i < 1000; i++) {
            tx.set("t"+i, "t"+i);
        }
        List<Object> exec = tx.exec();

        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");

    }

    @Test
    public void TestPipelined(){
        jedis.flushAll();
        Pipeline pipelined = jedis.pipelined();
        long start = System.currentTimeMillis();
        Transaction tx = jedis.multi();

        for (int i = 0; i < 1000; i++) {
            tx.set("p"+i, "p"+i);
        }
        List<Object> exec = pipelined.syncAndReturnAll();

        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");
    }

    @Test
    public void TestPipelinedTrans(){
        jedis.flushAll();
        //批量操作
        Pipeline pipelined = jedis.pipelined();
        long start = System.currentTimeMillis();
        pipelined.multi();//开启事物
        Transaction tx = jedis.multi();

        for (int i = 0; i < 100000; i++) {
            tx.set("p"+i, "p"+i);
        }
        pipelined.exec();
        System.out.println(tx.get("p1"));
        //开启事物报错
        List<Object> objects = pipelined.syncAndReturnAll();

        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");
    }
    @Test
    public void TestShard(){
        jedis.flushAll();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            String result = shard.set("shard" + i, "shard" + i);
        }
        //开启事物报错

        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");
    }
    @Test
    public void TestShardPipeline(){
        jedis.flushAll();
        ShardedJedisPipeline pipelined = shard.pipelined();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            pipelined.set("sp" + i, "sp" + i);
        }
        //开启事物报错
        List<Object> objects = pipelined.syncAndReturnAll();
        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");
    }

    @Test
    public void TestShardPool(){
        jedis.flushAll();
        ShardedJedis sj = pool.getResource();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            sj.set("sp" + i, "sp" + i);
        }
        //开启事物报错
        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000.0+"seconds");
    }

}