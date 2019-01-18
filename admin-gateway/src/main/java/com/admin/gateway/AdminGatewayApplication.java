package com.admin.gateway;

import com.admin.gateway.filter.ElapsedGatewayFilterFactory;
import com.admin.gateway.filter.RateLimitByIpGatewayFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminGatewayApplication.class, args);
    }

    @Bean
    public ElapsedGatewayFilterFactory elapsedGatewayFilterFactory() {
        return new ElapsedGatewayFilterFactory();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(
                        r -> r.path("/admin-core/{segment}")//路由条件
                                .filters(
                                        f -> f.stripPrefix(1)//去除请求第一层
                                                .filter(new RateLimitByIpGatewayFilter(10, 1, Duration.ofSeconds(2)))//ip限流
                                        //.filter()//token认证
                                )//过滤器
                                .uri("lb://admin-core")//目标服务地址
                                .order(0)
                                .id("admin-core")
                )
                .build();
    }
}

