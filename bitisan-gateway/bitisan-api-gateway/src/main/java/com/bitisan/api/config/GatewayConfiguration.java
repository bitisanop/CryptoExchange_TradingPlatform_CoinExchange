package com.bitisan.api.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /***
     * 熔断降级异常处理
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /***
     * 获取当前Route并按照当前Sentinel流量控制规则做处理
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }


    /***
     * 规则和Api加载
     */
    @PostConstruct
    public void doInit(){
        initCustomizedApis();
        initGatewayRules();
    }

    /****
     * 定义Api组
     */
    private void initCustomizedApis(){
        //定义集合存储要定义的API组
        Set<ApiDefinition> definitions = new HashSet<ApiDefinition>();

        //创建每个Api，并配置相关规律
        ApiDefinition cartApi = new ApiDefinition("mall_cart_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>(){{
                        // /cart/list
                        add(new ApiPathPredicateItem().setPattern("/cart/list"));
                        // /cart/*/*
                        add(new ApiPathPredicateItem().setPattern("/cart/**")
                        //根据前缀匹配
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});

        //将创建好的Api添加到Api集合中
        definitions.add(cartApi);
        //手动加载Api到Sentinel
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }


    /***
     * 限流规则定义
     */
    public void initGatewayRules(){
        //创建集合存储所有规则
        Set<GatewayFlowRule> rules = new HashSet<GatewayFlowRule>();

        //创建新的规则，并添加到集合中
        rules.add(new GatewayFlowRule("goods_route")
                //请求的阈值
                .setCount(6)
                //突发流量额外允许并发数量
                .setBurst(2)
                //限流行为
                //CONTROL_BEHAVIOR_RATE_LIMITER  匀速排队
                //CONTROL_BEHAVIOR_DEFAULT  直接失败
                //.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
                //排队时间
                //.setMaxQueueingTimeoutMs(10000)
                //统计时间窗口，单位：秒，默认为1秒
                .setIntervalSec(30));

        //创建新的规则，并添加到集合中
        rules.add(new GatewayFlowRule("mall_cart_api")
                //请求的阈值
                .setCount(2)
                //统计时间窗口，单位：秒，默认为1秒
                .setIntervalSec(2));
        //手动加载规则配置
        GatewayRuleManager.loadRules(rules);
    }
}
