/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @Author: sanfeng
 * @Date: 2018/3/15 10:57
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2018/3/15 10:57, Create
 */
package com.bitisan.netty.server;

import com.bitisan.core.configuration.NettyProperties;
import com.bitisan.netty.websocket.WebSocketChannelInitializer;
import io.netty.channel.ChannelInitializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import static com.bitisan.core.core.common.NettySpringContextUtils.getApplicationContext;

/**
 * <p>Description: </p>
 *
 * @Author: sanfeng
 * @Date: 2018/3/15 10:57
 */

public class NettyApplicationStartup implements ApplicationRunner {
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
////        String displayName = event.getApplicationContext().getDisplayName();
////        if(displayName.startsWith("FeignContext")){
////            return;
////        }
//
//    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //在容器加载完毕后获取dao层来操作数据库
        NettyProperties nettyProperties = getApplicationContext().getBean(NettyProperties.class);
        //在容器加载完毕后获取配置文件中的配置
        ChannelInitializer hawkServerInitializer = getApplicationContext().getBean(HawkServerInitializer.class);
        //在容器加载完毕后启动线程
        Thread thread = new Thread(new NettyServer(nettyProperties.getPort(), nettyProperties.getBossThreadSize(),
                nettyProperties.getWorkerThreadSize(), hawkServerInitializer));
        thread.start();
        if (nettyProperties.getWebsocketFlag() == 1) {//配置了websocket支持,启动websocket服务
            ChannelInitializer webSocketChannelInitializer = getApplicationContext().getBean(WebSocketChannelInitializer.class);
            Thread websocketThread = new Thread(new NettyServer(nettyProperties.getWebsocketPort(), nettyProperties.getBossThreadSize(),
                    nettyProperties.getWorkerThreadSize(), webSocketChannelInitializer));
            websocketThread.start();

        }
    }
}
