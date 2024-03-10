package com.bitisan.admin.task;

import com.bitisan.admin.dao.MemberLogDao;
import com.bitisan.admin.entity.ExchangeTurnoverStatistics;
import com.bitisan.admin.entity.MemberLog;
import com.bitisan.admin.entity.TurnoverStatistics;
import com.bitisan.constant.TransactionTypeEnum;
import com.bitisan.exchange.entity.ExchangeOrder;
import com.bitisan.exchange.feign.ExchangeOrderFeign;
import com.bitisan.p2p.feign.OtcOrderFeign;
import com.bitisan.user.entity.MemberDeposit;
import com.bitisan.user.feign.MemberDepositFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.WithdrawFeign;
import com.bitisan.user.vo.WithdrawVO;
import com.bitisan.util.DateUtil;
import com.bitisan.vo.OtcOrderVO;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Component
@Slf4j
public class MemberStatisticsJob {

    private static Logger logger = LoggerFactory.getLogger(MemberStatisticsJob.class);

    @Autowired
    private MemberFeign memberFeign ;

    @Autowired
    private OtcOrderFeign otcOrderFeign;

    @Autowired
    private ExchangeOrderFeign exchangeOrderFeign;

    @Autowired
    private MemberDepositFeign memberDepositFeign ;

    @Autowired
    private MongoTemplate mongoTemplate ;

    @Autowired
    private WithdrawFeign withdrawFeign ;

    @Autowired
    private MemberLogDao memberLogDao ;

    /**
     * 会员注册/实名/认证商家 统计
     */
//    @Scheduled(cron = "0 34 3 * * ?")
    @XxlJob("statisticsMember")
    public void statisticsMember() {
        try {
            if(!mongoTemplate.collectionExists("member_log")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    statisticsMember(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                statisticsMember(dateStr,date);
            }
        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }

    }

    /**
     * 法币/充币/提币 手续费
     * 币币交易手续费 统计
     * 法币成交量/成交额 统计
     */
//    @Scheduled(cron = "0 24 3 * * ?")
    @XxlJob("turnoverStatistics")
    public void turnoverStatistics() {
        try {
            if(!mongoTemplate.collectionExists("turnover_statistics")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    statisticsFee(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                statisticsFee(dateStr,date);
            }

        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }

    }

    /**
     * 币币交易成交量/成交额 统计
     */
    //    @Scheduled(cron = "0 14 3 * * ?")
    @XxlJob("exchangeStatistics")
    public void exchangeStatistics(){
        try {
            if(!mongoTemplate.collectionExists("exchange_turnover_statistics")){
                List<Date> list = getDateList();
                String dateStr = "";
                for(Date date :list){
                    dateStr = DateUtil.YYYY_MM_DD.format(date) ;
                    exchangeStatistics(dateStr,date);
                }
            }else{
                Date date = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
                String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);
                exchangeStatistics(dateStr,date);
            }

        } catch (ParseException e) {
            logger.error("日期解析异常",e);
        }
    }

    private void statisticsMember(String dateStr,Date date) throws ParseException {
        logger.info("开始统计会员信息{}",dateStr);
        int registrationNum = memberFeign.getRegistrationNum(dateStr);
        int bussinessNum = memberFeign.getBussinessNum(dateStr);
        int applicationNum = memberFeign.getApplicationNum(dateStr);
        MemberLog memberLog = new MemberLog();
        memberLog.setApplicationNum(applicationNum);
        memberLog.setBussinessNum(bussinessNum);
        memberLog.setRegistrationNum(registrationNum);
        memberLog.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
        memberLog.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
        //Calendar month 默认从0开始，方便起见 保存月份从1开始
        memberLog.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
        memberLog.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
        logger.info("{}会员信息{}",dateStr,memberLog);
        memberLogDao.save(memberLog);
        logger.info("结束统计会员信息{}",dateStr);
    }

    private List<Date> getDateList() throws ParseException {
        List<Date> list = new ArrayList<>() ;

        Date date = memberFeign.getStartRegistrationDate();
        String dateStr = DateUtil.YYYY_MM_DD.format(date) ;
        date = DateUtil.YYYY_MM_DD.parse(dateStr);

        Calendar calendar = Calendar.getInstance() ;
        calendar.setTime(date);
        Date endDate = DateUtil.dateAddDay(new Date(),-1) ;
        while(date.before(endDate)){
            list.add(date);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            date = calendar.getTime() ;
        }
        return list;
    }

    private void statisticsFee(String dateStr,Date date) throws ParseException {
        /**
         * 法币成交
         *
         */
        logger.info("开始统计法币成交信息{}",dateStr);
        List<OtcOrderVO> list1 = otcOrderFeign.getOtcOrderStatistics(dateStr);
        TurnoverStatistics turnoverStatistics = new TurnoverStatistics();
        turnoverStatistics.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
        turnoverStatistics.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
        //Calendar month 默认从0开始，方便起见 保存月份从1开始
        turnoverStatistics.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
        turnoverStatistics.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
        for(OtcOrderVO ord:list1){
            /**
             * 法币成交量/手续费
             */
            turnoverStatistics.setUnit(ord.getUnit());
            turnoverStatistics.setAmount(ord.getNumber());
            turnoverStatistics.setFee(ord.getFee());
            turnoverStatistics.setType(TransactionTypeEnum.OTC_NUM);
            logger.info("{}法币成交信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");

            /**
             * 法币成交额
             */
            turnoverStatistics.setAmount(ord.getMoney());
            turnoverStatistics.setType(TransactionTypeEnum.OTC_MONEY);
            turnoverStatistics.setFee(null);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计法币成交信息{}",dateStr);

        /**
         * 币币成交额
         */
        logger.info("开始统计币币成交额信息{}",dateStr);
        turnoverStatistics.setFee(null);
        List<ExchangeOrder> list2 = exchangeOrderFeign.getExchangeTurnoverBase(dateStr);
        for(ExchangeOrder order:list2){
            turnoverStatistics.setUnit(order.getBaseSymbol());
            turnoverStatistics.setAmount(order.getTurnover());
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE_BASE);
            logger.info("{}币币成交额信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币成交额信息{}",dateStr);

        /**
         * 币币成交量
         */
        logger.info("开始统计币币成交量信息{}",dateStr);
        List<ExchangeOrder> list3 = exchangeOrderFeign.getExchangeTurnoverCoin(dateStr);
        for(ExchangeOrder order:list3){
            turnoverStatistics.setUnit(order.getCoinSymbol());
            turnoverStatistics.setAmount(order.getTradedAmount());
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE_COIN);
            logger.info("{}币币成交量信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币成交量信息{}",dateStr);

        /**
         * 充币
         */
        logger.info("开始统计充币信息{}",dateStr);
        List<MemberDeposit> list4 = memberDepositFeign.getDepositStatistics(dateStr);
        for(MemberDeposit deposit:list4){
            turnoverStatistics.setAmount(deposit.getAmount());
            turnoverStatistics.setUnit(deposit.getUnit());
            turnoverStatistics.setType(TransactionTypeEnum.RECHARGE);
            logger.info("{}充币信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计充币信息{}",dateStr);

        /**
         * 币币交易手续费
         */
        logger.info("开始统计币币交易手续费信息{}",dateStr);
        ProjectionOperation projectionOperation = Aggregation.project("time","type","unit","fee");

        Criteria operator = Criteria.where("coinName").ne("").andOperator(
                Criteria.where("time").gte(DateUtil.YYYY_MM_DD_MM_HH_SS.parse(dateStr+" 00:00:00").getTime()),
                Criteria.where("time").lte(DateUtil.YYYY_MM_DD_MM_HH_SS.parse(dateStr+" 23:59:59").getTime()),
                Criteria.where("type").is("EXCHANGE")
        );

        MatchOperation matchOperation = Aggregation.match(operator);

        GroupOperation groupOperation = Aggregation.group("unit","type").sum("fee").as("feeSum") ;

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);
        // 执行操作
        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "order_detail_aggregation", Map.class);
        List<Map> list = aggregationResults.getMappedResults();
        for(Map map:list){
            logger.info("*********{}币币交易手续费{}************",dateStr,map);
            turnoverStatistics.setFee(new BigDecimal(map.get("feeSum").toString()));
            turnoverStatistics.setAmount(null);
            turnoverStatistics.setUnit(map.get("unit").toString());
            turnoverStatistics.setType(TransactionTypeEnum.EXCHANGE);
            logger.info("{}币币交易手续费信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计币币交易手续费信息{}",dateStr);

        /**
         * TODO 提币 待处理
         */
        logger.info("开始统计提币信息{}",dateStr);
        List<WithdrawVO> list5 = withdrawFeign.getWithdrawStatistics(dateStr);
        for(WithdrawVO vo:list5){
            turnoverStatistics.setFee(vo.getTotalFee());
            turnoverStatistics.setAmount(vo.getAmount());
            turnoverStatistics.setUnit(vo.getUnit());
            turnoverStatistics.setType(TransactionTypeEnum.WITHDRAW);
            logger.info("{}提币信息{}",dateStr,turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics,"turnover_statistics");
        }
        logger.info("结束统计提币信息{}",dateStr);
    }

    private void exchangeStatistics(String dateStr,Date date) throws ParseException {
        /**
         * 币币成交(按照交易对统计)
         */
        logger.info("开始统计币币成交(按照交易对统计)信息{}",dateStr);
        List<ExchangeOrder> list = exchangeOrderFeign.getExchangeTurnoverSymbol(dateStr);
        ExchangeTurnoverStatistics exchangeTurnoverStatistics = new ExchangeTurnoverStatistics() ;
        for(ExchangeOrder order:list){
            exchangeTurnoverStatistics.setDate(DateUtil.YYYY_MM_DD.parse(dateStr));
            exchangeTurnoverStatistics.setAmount(order.getTradedAmount());
            exchangeTurnoverStatistics.setBaseSymbol(order.getBaseSymbol());
            exchangeTurnoverStatistics.setCoinSymbol(order.getCoinSymbol());
            exchangeTurnoverStatistics.setMoney(order.getTurnover());
            exchangeTurnoverStatistics.setYear(DateUtil.getDatePart(date,Calendar.YEAR));
            //Calendar month 默认从0开始，方便起见 保存月份从1开始
            exchangeTurnoverStatistics.setMonth(DateUtil.getDatePart(date,Calendar.MONTH)+1);
            exchangeTurnoverStatistics.setDay(DateUtil.getDatePart(date,Calendar.DAY_OF_MONTH));
            logger.info("{}币币成交(按照交易对统计)信息{}",dateStr,exchangeTurnoverStatistics);
            mongoTemplate.insert(exchangeTurnoverStatistics,"exchange_turnover_statistics");
        }
        logger.info("结束统计币币成交(按照交易对统计)信息{}",dateStr);
    }

}
