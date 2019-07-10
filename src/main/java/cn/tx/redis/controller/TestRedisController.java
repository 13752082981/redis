package cn.tx.redis.controller;

import cn.tx.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestRedisController {
    @Autowired
    private RedisUtil util;


    @RequestMapping(value="test")
    public String test(){
        for (int i = 0; i < 100; i++) {
            util.set("name"+i, ""+i);
        }

        System.out.println(util.get("name"));
        return util.get("name");
    }
}
