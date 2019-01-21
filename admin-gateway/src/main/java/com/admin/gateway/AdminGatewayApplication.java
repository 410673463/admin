package com.admin.gateway;

import com.admin.gateway.filter.ElapsedGatewayFilterFactory;
import com.admin.gateway.filter.IpRateLimitGatewayFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
    /**
     * 自定义限流标志的key，多个维度可以从这里入手
     * exchange对象中获取服务ID、请求信息，用户信息等
     */
   /* @Bean
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }*/

    /**
     * 用户限流 使用这种方式限流，请求路径中必须携带userId参数
     * @return
     */
   /* @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }*/

    /**
     * 接口限流 获取请求地址的uri作为限流key
     *
     * @return
     */
    /*@Bean
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }*/
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(
                        r -> r.path("/admin-core/{segment}")//路由条件
                                .filters(
                                        f -> f.stripPrefix(1)//去除请求第一层
                                                .filter(new IpRateLimitGatewayFilter(2, 1, "spring:gateway:ratelimit:ip:"))
                                        //.filter()//token认证
                                )//过滤器
                                .uri("lb://admin-core")//目标服务地址
                                .order(0)
                                .id("admin-core")
                )
                .build();
    }
}

