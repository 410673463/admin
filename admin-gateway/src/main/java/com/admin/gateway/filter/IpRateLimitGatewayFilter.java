package com.admin.gateway.filter;

import com.admin.gateway.util.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@CommonsLog
@Data
@Builder
@AllArgsConstructor
public class IpRateLimitGatewayFilter implements GatewayFilter, Ordered {
    // 限流阈值,单位时间内访问次数
    private long limitTimes;
    // 限流超时时间，多上时间清除，单位秒
    private long expireTime;
    private String key;




    private static DefaultRedisScript<Long> redisScript;

    private static RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(redisTemplate == null){
            redisTemplate = (RedisTemplate) SpringUtils.getBean("redisTemplate");
        }
        if(redisScript == null){
            initRedisScript();
        }
        /**
         * 执行Lua脚本
         */
        List<String> keyList = new ArrayList();
        // 设置key值为注解中的值
        keyList.add(key);
        /**
         * 调用脚本并执行
         */
        Long result = (Long) redisTemplate.execute(redisScript, keyList, expireTime, limitTimes);

        if (result == 0) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }else {
            return chain.filter(exchange);
        }
    }


    public void initRedisScript() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis-rate-limiter.lua")));
        log.info("RateLimterHandler[分布式限流处理器]脚本加载完成");
    }


    @Override
    public int getOrder() {
        return 0;
    }


}
