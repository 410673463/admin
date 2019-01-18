package com.admin.modules.core;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class HelloController {

    @Value("${server.port}")
    private String port;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @RequestMapping("/hello")
    public String hello(){
        String s = redisTemplate.opsForValue().get("FI:DEFAULT:CURRENCY");

        return  "hello:"+port+"-coin:"+s;
    }

}
