package com.admin.gateway;

import com.admin.gateway.filter.ElapsedGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
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
  /*  @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        StripPrefixGatewayFilterFactory.Config config = new StripPrefixGatewayFilterFactory.Config();
        config.setParts(1);
        return builder.routes()
                .route(
                        r -> r.path("/admin-core/**")//路由条件
                        .filters(f -> f.stripPrefix(1).filter(new Hystrix()))//过滤器
                        .uri("lb://admin-core")//目标服务地址
                        .order(0)//排序
                        .id("admin-core")
                )
                .build();
    }*/

}

