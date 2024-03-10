package com.bitisan.market.util;


import com.bitisan.market.client.Client;
import com.bitisan.market.socket.ws.WebSocketHuobi;

public class WebSocketConnectionManage {

    private static Client client;
    public static WebSocketHuobi ws; // 价格监听websocket
    public static Client getClient() { return client; }
    public static void setClient(Client client) {
        WebSocketConnectionManage.client = client;
    }

    public static WebSocketHuobi getWebSocket() { return ws; }
    public static void setWebSocket(WebSocketHuobi ws) { WebSocketConnectionManage.ws = ws; }
}
