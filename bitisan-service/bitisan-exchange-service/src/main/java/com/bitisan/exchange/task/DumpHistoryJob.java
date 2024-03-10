package com.bitisan.exchange.task;

import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.entity.ExchangeOrderDetail;
import com.bitisan.exchange.service.ExchangeOrderService;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.service.MemberTransactionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 清理机器人订单（5天前订单）
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 *
 */
@Component
@Slf4j
public class DumpHistoryJob {

	@Autowired
	private ExchangeOrderService exchangeOrderService;
    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private MongoTemplate mongoTemplate ;

    /**
     *  每天3点20分执行一次
     *  注意，删除订单主表和详情表时，只删除机器人数据（详见数据层的sql实现）
     * */
	@XxlJob("deleteHistoryOrders")
	public void deleteHistoryOrders(){
		log.info("开始清理交易历史数据");
		long beforeTime = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000); // 2天前
		log.info("清除指定时间之前的订单："+beforeTime);
		int limit = 1000;
		int deleteTime = 1;
		int deleteCount = 0;
		List<ExchangeOrder> list = exchangeOrderService.queryHistoryDelete(beforeTime,limit);
		boolean hashNext =true;
		while (hashNext) {
			hashNext = false;
			if (list != null && list.size() > 0) {
				deleteCount = deleteCount + list.size();
				Set<String> orderIds = new HashSet<>();
				for (int i = 0; i < list.size(); i++) {
					ExchangeOrder exchangeOrder = list.get(i);
					// 清除mongodb中的交易详细
					orderIds.add(exchangeOrder.getOrderId());
//					log.info("清理订单明细：" + exchangeOrder.getOrderId());
//					exchangeOrderService.delete(exchangeOrder.getOrderId());

				}
				//批量删除
				if(orderIds.size()>0) {
					log.info("批量清理订单数：" + orderIds.size());
					Query query = new Query(Criteria.where("orderId").in(orderIds));
					log.info("开始清理mongo：" + orderIds.size());
					mongoTemplate.remove(query, ExchangeOrderDetail.class);
					log.info("开始清理数据库：" + orderIds.size());
					exchangeOrderService.removeByIds(orderIds);
					log.info("清理完成：" + orderIds.size());
				}

				log.info("第{}轮清除数据完成,已清除{}条数据",deleteTime,deleteCount);
				deleteTime++;
				if (list.size() == limit) {
					hashNext = true;
					log.info("获取第{}轮数据,进行数据清理",deleteTime);
					list = exchangeOrderService.queryHistoryDelete(beforeTime,limit);
				}
			}
		}
		log.info("清除交易订单：" + deleteCount + "条");

		Date today = new Date();
		Calendar now =Calendar.getInstance();
		now.setTime(today);
		now.set(Calendar.DATE,now.get(Calendar.DATE) - 5); // 清理5天前数据
		Date startTime = now.getTime();

		log.info("清除资产变更记录时间：" + startTime.getTime());

		int tCount = memberTransactionService.deleteHistory(startTime);

		log.info("清除资产变更记录数量：" + tCount);
		log.info("结束清理交易历史数据");
	}

}
