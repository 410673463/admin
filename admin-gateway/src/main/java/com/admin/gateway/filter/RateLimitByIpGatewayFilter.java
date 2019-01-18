package com.admin.gateway.filter;


import com.admin.gateway.util.SpringUtils;
import com.netflix.discovery.converters.Auto;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Ip限流
 */


@CommonsLog
@Data
@Builder
@Component
public class RateLimitByIpGatewayFilter implements GatewayFilter, Ordered {
    private int capacity;//桶的最大容量，即能装载 Token 的最大数量
    private int refillTokens;//每次 Token 补充量
    private Duration refillDuration;//补充 Token 的时间间隔

    @Autowired
    private static RedisTemplate<String,Object> redisTemplate;

    public RateLimitByIpGatewayFilter() {

    }

    public RateLimitByIpGatewayFilter(int capacity, int refillTokens, Duration refillDuration) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillDuration = refillDuration;
    }

    private Bucket createNewBucket(){
        Refill refill = Refill.greedy(refillTokens,refillDuration);
        Bandwidth limit = Bandwidth.classic(capacity,refill);
        return Bucket4j.builder().addLimit(limit).build();
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        if(redisTemplate == null){
            redisTemplate = (RedisTemplate<String, Object>) SpringUtils.getBean("redisTemplate");
        }
        /**
         * 每个IP生成一个桶，每次请求从判断redis中是否存在桶，
         * 有桶，访问过，判断是否有剩余,拿此桶继续
         * 没有桶创建一个新的继续
         */
        String keyConfig = "GATEWAY:RATE:LIMIT:IP:";
        Bucket bucket = (Bucket) redisTemplate.opsForValue().get(keyConfig+ip);
        if(bucket == null){
            bucket = createNewBucket();
        }
        log.debug("IP: " + ip + ", TokenBucket Available Tokens: " + bucket.getAvailableTokens());
        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1000;
    }


}
