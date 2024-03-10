//package com.bitisan.api.mq;
//
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///*****
// * @Author:
// * @Description:
// ****/
//@Component
//@RocketMQMessageListener(topic = "member-register-swap",consumerGroup = "gateway-group")
//public class RefundResultListener implements RocketMQListener,RocketMQPushConsumerLifecycleListener {
//
//
//    @Override
//    public void onMessage(Object message) {
//    }
//
//    /****
//     * 消息监听
//     * @param consumer
//     */
//    @Override
//    public void prepareStart(DefaultMQPushConsumer consumer) {
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                try {
//                    for (MessageExt msg : msgs) {
//                        //AES加密字符串
//                        String result = new String(msg.getBody(),"UTF-8");
//
//                        System.out.println("退款申请状态---result:"+result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//    }
//}
