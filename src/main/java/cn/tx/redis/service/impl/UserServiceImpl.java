package cn.tx.redis.service.impl;

import cn.tx.redis.domain.User;
import cn.tx.redis.mapper.UserMapper;
import cn.tx.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
     private UserMapper userMapper;

    @Cacheable(value = "findAll", key = "'test'")
     public List<User> findAll() {
         System.out.println("如果没打印这句话，说明走了缓存！");
         return userMapper.findAll();
     }
}
