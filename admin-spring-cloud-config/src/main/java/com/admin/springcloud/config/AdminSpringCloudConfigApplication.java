package com.admin.springcloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class AdminSpringCloudConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSpringCloudConfigApplication.class, args);
    }

}

