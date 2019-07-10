package cn.tx.redis.service;

import cn.tx.redis.domain.User;

import java.util.List;

public interface UserService {

    public List<User> findAll();
}
