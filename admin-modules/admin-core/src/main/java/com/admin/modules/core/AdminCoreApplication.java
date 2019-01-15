package com.admin.modules.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class AdminCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminCoreApplication.class, args);
    }

}

