/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @Author: sanfeng
 * @Date: 2018/3/19 10:53
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2018/3/19 10:53, Create
 */
package com.bitisan.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * <p>Description: </p>
 *
 * @Author: sanfeng
 * @Date: 2018/3/19 10:53
 */
public class  WebSocketFramePrepender extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        WebSocketFrame webSocketFrame = new BinaryWebSocketFrame(msg);
        out.add(webSocketFrame.retain());
    }
}
