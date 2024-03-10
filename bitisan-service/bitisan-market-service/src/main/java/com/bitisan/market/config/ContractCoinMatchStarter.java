package com.bitisan.market.config;

import com.bitisan.market.client.Client;
import com.bitisan.market.service.KlineRobotMarketService;
import com.bitisan.market.socket.client.WsClientHuobi;
import com.bitisan.market.util.WebSocketConnectionManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ContractCoinMatchStarter implements ApplicationRunner {

    private Logger log = LoggerFactory.getLogger(ContractCoinMatchStarter.class);

    @Autowired
    private Client client;
    @Autowired
    private KlineRobotMarketService klineRobotMarketService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        WebSocketConnectionManage.setClient(client);
        WsClientHuobi w = new WsClientHuobi();
        w.setContractMarketService(klineRobotMarketService);
        w.run();
    }
}
