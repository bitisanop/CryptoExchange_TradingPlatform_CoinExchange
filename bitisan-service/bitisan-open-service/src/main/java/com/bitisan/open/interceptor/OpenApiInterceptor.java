package com.bitisan.open.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.bitisan.user.entity.MemberApiKey;
import com.bitisan.open.util.HmacSHA256Signer;
import com.bitisan.open.util.RedisUtil;
import com.bitisan.user.feign.MemberApiKeyFeign;
import com.bitisan.util.IPUtils;
import com.bitisan.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: OpenApiInterceptor
 * api拦截器
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 * @create: 2019/05/06 14:11
 */
@Slf4j
public class OpenApiInterceptor implements HandlerInterceptor {


    @Autowired
    private MemberApiKeyFeign memberApiKeyService;
    @Autowired
    private RedisUtil redisUtil;
    private static final String API_HOST = "39.100.79.158";
    private static final String SIGNATURE_METHOD = "HmacSHA256";
    private static final String SIGNATURE_VERSION = "2";

    private static final ZoneId ZONE_GMT = ZoneId.of("Z");

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        //获取请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("A={}",JSONObject.toJSONString(parameterMap));
        Map<String,String> params = new TreeMap<>();
        for (Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            String[] values =  parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        String accessKeyId = params.get("accessKeyId");
        String apiKey ="";
        if(accessKeyId!=null){
            apiKey = accessKeyId;
        }
        String signature = params.get("signature");
        if(StringUtils.isEmpty(signature)){
            this.ajaxReturn(response,3002,"签名有误，请核实");
            return false;
        }
        if(StringUtils.isEmpty(apiKey)){
            this.ajaxReturn(response,3000,"参数有误，请核实");
            return false;
        }
        String timestamp = params.get("timestamp");
        if(StringUtils.isEmpty(timestamp)){
            this.ajaxReturn(response,3003,"时间戳有误，请核实");
            return false;
        }

        //根据apiKey获取用户信息
        //解决service为null无法注入问题
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        MemberApiKey memberApiKey = memberApiKeyService.findMemberApiKeyByApiKey(apiKey);
        if(memberApiKey==null){
            this.ajaxReturn(response,3001,"apiKey有误，请核实");
            return false;
        }
        //判断ip
        String remoteIp = IPUtils.getIpAddr(request);
        //限制ip调用次数 10s 一次
        Object o = redisUtil.get(remoteIp);
        if(o!=null){
            this.ajaxReturn(response,3005,"调用频繁，请稍后");
            return false;
        }
        redisUtil.set(remoteIp,"limit",10,TimeUnit.SECONDS);

        String ips = memberApiKey.getBindIp();
        if(StringUtils.isNotEmpty(ips)){
            String[] split = ips.split(",");
            List<String> ipList = Arrays.asList(split);
            if(!ipList.contains(remoteIp)){
                this.ajaxReturn(response,3004,"IP有误，请核实");
                return false;
            }
        }

        //创建签名
        String method = request.getMethod();
        String path = request.getRequestURI();

        //移除重复
//        params.remove("accessKeyId");
//        params.remove("timestamp");
        params.remove("signature");
        params.remove("signatureMethod");
        params.remove("signatureVersion");

        String  sign= createSignature(params,memberApiKey.getSecretKey());
        if(!signature.equals(sign)){
            this.ajaxReturn(response,3002,"签名有误，请核实");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private static String createSignature(Map<String,String> map,String secretKey) {

        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,String> entry:map.entrySet()){
            if(entry.getValue() != null && StringUtils.isNotBlank(entry.getValue()) && !"null".equals(entry.getValue())
                    && !"class".equals(entry.getKey()) && !"data".equals(entry.getKey())){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        //过滤最后一个字符串&
        int lastIdx = result.lastIndexOf("&");
        result = result.substring(0,lastIdx);
        result +=  secretKey;
        try{
            result = MD5.md5(result).toUpperCase();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String encode(String code) {
        try {
            return URLEncoder.encode(code, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void ajaxReturn(HttpServletResponse response, int code, String msg) throws IOException, JSONException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        org.json.JSONObject json = new org.json.JSONObject();
        json.put("code", code);
        json.put("message", msg);
        out.print(json.toString());
        out.flush();
        out.close();
    }

}
