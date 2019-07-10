package cn.tx.redis.controller;

import cn.tx.redis.domain.User;
import cn.tx.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
 public class UserController_YML {

    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public List<User> getUsers(){
     return userService.findAll();
    }

}