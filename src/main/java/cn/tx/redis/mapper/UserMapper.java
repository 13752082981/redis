package cn.tx.redis.mapper;

import cn.tx.redis.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    public List<User> findAll();
}
