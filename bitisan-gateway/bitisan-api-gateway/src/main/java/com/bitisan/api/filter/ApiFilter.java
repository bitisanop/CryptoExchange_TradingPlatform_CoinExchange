package com.bitisan.api.filter;

import com.bitisan.api.permission.AuthorizationInterceptor;
import com.bitisan.constant.SysConstant;
import com.bitisan.permission.entity.Permission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApiFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    /***
     * 执行拦截处理      http://localhost:9001/mall/seckill/order?id&num
     *                 JWT
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //是否禁止
        Permission forbidUrl = authorizationInterceptor.isForbidUrl(exchange);
        if(forbidUrl!=null){
            return endProcess(exchange,403,"Not allowed to pass～url bad");
        }

        //是否需要拦截
        Permission permission = authorizationInterceptor.isIntercept(exchange);
        if(permission==null){
            return chain.filter(exchange);
        }

        //令牌校验
        Map<String, Object> resultMap = authorizationInterceptor.tokenIntercept(exchange);

        if(resultMap==null ){
//            || !authorizationInterceptor.rolePermission(resultMap,permission) 不再判断
            //令牌校验失败 或者没有权限
            return endProcess(exchange,4000,"Access denied");
        }

        ServerHttpRequest host = null;
        try {
            host = exchange.getRequest().mutate().header(SysConstant.SESSION_MEMBER, URLEncoder.encode(resultMap.get(SysConstant.SESSION_MEMBER).toString(),"UTF-8")).build();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        //将现在的request 变成 change对象
        exchange = exchange.mutate().request(host).build();

        //秒杀过滤
//        if(uri.equals("/seckill/order")){
//            seckillFilter(exchange, request, resultMap.get("username").toString());
//            return chain.filter(exchange);
//        }

        //NOT_HOT 直接由后端服务处理
        return chain.filter(exchange);
    }



    /****
     * 结束程序
     * @param exchange
     * @param code
     * @param message
     */
    public Mono<Void> endProcess(ServerWebExchange exchange,Integer code,String message){
        //响应状态码200
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("code",code);
        resultMap.put("message",message);
        return Mono.defer(() -> {
            byte[] bytes = new byte[0];
            try {
                bytes = new ObjectMapper().writeValueAsBytes(resultMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        });

    }

    @Override
    public int getOrder() {
        return 10001;
    }
}
