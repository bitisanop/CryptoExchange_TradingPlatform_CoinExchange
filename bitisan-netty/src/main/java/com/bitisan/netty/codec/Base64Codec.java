package com.bitisan.netty.codec;


import io.netty.channel.Channel;

import java.util.Base64;

/**
 *
 * <p>Title: Base64Codec</p>
 * <p>Description: </p>
 * Base64加密加密
 * @author MrGao
 * @date 2019年6月26日
 */
public class Base64Codec implements Codec {

    @Override
    public byte[] decrypt(Channel channel, byte[] body) {
        return Base64.getDecoder().decode(body);
    }

    @Override
    public byte[] encrypt(Channel channel, byte[] body) {
        return Base64.getEncoder().encode(body);
    }
}
