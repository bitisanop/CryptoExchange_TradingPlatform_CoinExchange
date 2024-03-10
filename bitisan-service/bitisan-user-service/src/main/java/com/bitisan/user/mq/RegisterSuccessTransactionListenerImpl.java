//package com.bitisan.user.mq;
//
//import com.alibaba.fastjson.JSON;
//import com.bitisan.user.entity.Member;
//import com.bitisan.user.event.MemberEvent;
//import com.bitisan.user.service.MemberService;
//import com.bitisan.user.service.MemberWeightUpperService;
//import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.Message;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.Map;
//
//@Component
//@RocketMQTransactionListener(txProducerGroup = "registerSuccess")
//public class RegisterSuccessTransactionListenerImpl implements RocketMQLocalTransactionListener {
//
//    @Autowired
//    private MemberEvent memberEvent;
//    @Autowired
//    private MemberWeightUpperService memberWeightUpperService;
//    /**
//     * 如果值为1，推荐注册的推荐人必须被推荐人实名认证才能获得奖励
//     */
//    @Value("${commission.need.real-name:1}")
//    private int needRealName;
//    @Autowired
//    private MemberService memberService;
//
//    /****
//     * 变更订单状态，记录退款申请信息
//     * @param msg
//     * @param arg
//     * @return
//     */
//    @Override
//    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
//        try {
//            Map<String, Object> params = (Map<String, Object>) arg;
//            Member member = (Member) params.get("member");
//            String promotionCode = (String) params.get("promotionCode");
//            System.out.println("member:::"+JSON.toJSONString(member));
//            //推广活动
//            if (StringUtils.hasText(promotionCode)) {
//                Member member1 = memberService.findMemberByPromotionCode(promotionCode);
//                if (member1 != null) {
//                    member.setInviterId(member1.getId());
//                    //如果不需要实名认证，直接发放奖励
//                    if (needRealName == 0) {
//                        memberEvent.promotion(member1, member);
//                    }
//                }
//            }
//            //更新
//            memberService.updateById(member);
//            //增加upper关系
//            memberWeightUpperService.saveMemberWeightUpper(member);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return RocketMQLocalTransactionState.ROLLBACK;
//        }
//        return RocketMQLocalTransactionState.COMMIT;
//    }
//
//
//    /****
//     * 消息回查
//     * @param msg
//     * @return
//     */
//    @Override
//    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
//        return RocketMQLocalTransactionState.COMMIT;
//    }
//}
