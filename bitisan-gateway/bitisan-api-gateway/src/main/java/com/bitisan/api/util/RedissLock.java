package com.bitisan.api.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissLock {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPwd;

    /***
     * 创建RedissonClient对象
     *      创建锁、解锁
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        //创建Config
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort).setPassword(redisPwd);
        //集群实现
//        config.useClusterServers()
//                .setScanInterval(2000)
//                .addNodeAddress(
//                        "redis://192.168.100.130:7001",
//                        "redis://192.168.100.130:7002",
//                        "redis://192.168.100.130:7003",
//                        "redis://192.168.100.130:7004",
//                        "redis://192.168.100.130:7005",
//                        "redis://47.242.254.112:6379");
        //创建RedissonClient
        return Redisson.create(config);
    }
}
