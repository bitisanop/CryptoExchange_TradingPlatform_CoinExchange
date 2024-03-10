/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 *
 * <p>FileName: HawkSessionContext.java</p>
 *
 * Description:
 * @author MrGao
 * @date 2019年7月25日
 * @version 1.0
 * History:
 * v1.0.0, , 2019年7月25日, Create
 */
package com.bitisan.netty.shiro.mgt;

import com.bitisan.core.entity.RequestPacket;
import com.bitisan.core.entity.ResponsePacket;
import com.bitisan.netty.shiro.util.RequestPairSource;
import org.apache.shiro.session.mgt.SessionContext;

/**
 * <p>Title: HawkSessionContext</p>
 * <p>Description: </p>
 * @author MrGao
 * @date 2019年7月25日
 */
public interface HawkSessionContext extends SessionContext, RequestPairSource {
	 RequestPacket getHawkRequest();
	 void setHawkRequest(RequestPacket request);
	 ResponsePacket getHawkResponse();
	 void setHawkResponse(ResponsePacket response);
}
