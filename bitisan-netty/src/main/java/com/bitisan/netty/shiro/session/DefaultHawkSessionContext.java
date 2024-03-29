/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 *
 * <p>FileName: DefaultHawkSessionContext.java</p>
 *
 * Description:
 * @author MrGao
 * @date 2019年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2019年7月25日, Create
 */
package com.bitisan.netty.shiro.session;

import com.bitisan.core.entity.RequestPacket;
import com.bitisan.core.entity.ResponsePacket;
import com.bitisan.netty.shiro.mgt.HawkSessionContext;
import org.apache.shiro.session.mgt.DefaultSessionContext;

import java.util.Map;

/**
 * <p>Title: DefaultHawkSessionContext</p>
 * <p>Description: </p>
 * @author MrGao
 * @date 2019年7月25日
 */
public class DefaultHawkSessionContext extends DefaultSessionContext implements HawkSessionContext {

    private static final long serialVersionUID = -3974604687792523072L;

    private static final String HAWK_REQUEST = DefaultHawkSessionContext.class.getName() + ".HAWK_REQUEST";
    private static final String HAWK_RESPONSE = DefaultHawkSessionContext.class.getName() + ".HAWK_RESPONSE";

    public DefaultHawkSessionContext() {
        super();
    }

    public DefaultHawkSessionContext(Map<String, Object> map) {
        super(map);
    }
    @Override
    public void setHawkRequest(RequestPacket request) {
        if (request != null) {
            put(HAWK_REQUEST, request);
        }
    }
    @Override
    public RequestPacket getHawkRequest() {
        return getTypedValue(HAWK_REQUEST, RequestPacket.class);
    }
    @Override
    public void setHawkResponse(ResponsePacket response) {
        if (response != null) {
            put(HAWK_RESPONSE, response);
        }
    }
    @Override
    public ResponsePacket getHawkResponse() {
        return getTypedValue(HAWK_RESPONSE, ResponsePacket.class);
    }
}
