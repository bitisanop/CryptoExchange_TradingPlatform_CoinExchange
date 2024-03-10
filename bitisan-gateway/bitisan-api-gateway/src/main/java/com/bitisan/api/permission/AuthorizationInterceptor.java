package com.bitisan.api.permission;

import com.alibaba.fastjson.JSON;
import com.bitisan.api.util.IpUtil;
import com.bitisan.constant.SysConstant;
import com.bitisan.permission.entity.Permission;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.util.JwtToken;
import com.bitisan.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class AuthorizationInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /****
     * 判断uri是否为有效路径
     * @param uri
     * @return
     */
    public Boolean isInvalid(String uri){
        RBloomFilter<String> uriBloomFilterArray = redissonClient.getBloomFilter("UriBloomFilterArray");
        return uriBloomFilterArray.contains(uri);
    }

    /***
     * 权限校验
     */
    public Boolean rolePermission(Map<String, Object> token,Permission permission){
        //匹配->获取角色集合
        String[] roles = token.get("roles").toString().split(",");

        //循环判断每个角色是否有权限
        for (String role : roles) {

            Set<Integer> permissions = (Set<Integer>) redisTemplate.boundHashOps("RolePermissionMap").get("Role_" + role);

            if(permissions==null){
                continue;
            }
            //循环判断
           if(permissions.contains(permission.getId())){
               return true;
           }
        }
        return false;
    }

    /***
     * 令牌校验
     */
    public Map<String,Object> tokenIntercept(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        //校验其他地址
        String clientIp = IpUtil.getIp(request);
        //获取令牌
        String token = request.getHeaders().getFirst("x-auth-token");
        if(StringUtils.isEmpty(token)){
            return null;
        }
        //令牌校验
        Map<String, Object> resultMap = this.jwtVerify(token, clientIp);
        return resultMap;
    }

    /***
     * 是否需要拦截校验
     * true:需要拦截
     * false:不需要拦截
     */
    public Permission isIntercept(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        //获取uri  /cart/list
        String uri = request.getURI().getPath();
        //提交方式  GET/POST/*
        String method = request.getMethodValue();
        //服务名字
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();

        //从Redis缓存中进行匹配
        // 0:完全匹配
//        List<Permission> permissionsMatch0 = (List<Permission>) redisTemplate.boundHashOps("RolePermissionAll").get("PermissionMatch0");
//        Permission permission = null;
//        if(permissionsMatch0!=null){
            // 1:通配符匹配
        Permission permission = match0(uri, method, servicename,"permission");
//        }
        // 1:通配符匹配
        //如果permission==null，则执行通配符匹配
        if(permission==null){
            //通配符匹配 /cat/{string}/{integer}
            List<Permission> permissionsMatch1 = (List<Permission>) redisTemplate.boundHashOps("permission:1").values();
            if(permissionsMatch1!=null){
                permission = match1(permissionsMatch1,uri,method,servicename);
            }
            //如果通配符匹配也为空，表明当前请求不需要进行权限校验
        }
//        if(permission==null){
//            return false;
//        }
        return permission;
    }

    /***
     * 是否需要拦截校验
     * true:需要拦截
     * false:不需要拦截
     */
    public Permission isForbidUrl(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        //获取uri  /cart/list
        String uri = request.getURI().getPath();
        //提交方式  GET/POST/*
        String method = request.getMethodValue();
        //服务名字
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();

        //从Redis缓存中进行匹配
        // 0:完全匹配
//        List<Permission> permissionsMatch0 = (List<Permission>) redisTemplate.boundHashOps("RolePermissionAll").get("PermissionMatch0");
//        Permission permission = null;
//        if(permissionsMatch0!=null){
        // 1:通配符匹配
        Permission permission = match0(uri, method, servicename,"forbid");
//        }
        // 1:通配符匹配
        //如果permission==null，则执行通配符匹配
        if(permission==null){
            //通配符匹配 /cat/{string}/{integer}
            List<Permission> permissionsMatch1 = (List<Permission>) redisTemplate.boundHashOps("forbid:1").values();
            if(permissionsMatch1!=null){
                permission = match1(permissionsMatch1,uri,method,servicename);
            }
            //如果通配符匹配也为空，表明当前请求不需要进行权限校验
        }
//        if(permission==null){
//            return false;
//        }
        return permission;
    }

    /***
     * 匹配方法:完全匹配
     */
    public Permission match0(String uri,String method,String serviceName,String prefix){
        Object p = redisTemplate.boundHashOps(prefix+":0").get(serviceName+uri);
        if(!ObjectUtils.isEmpty(p)){
            Permission permission = (Permission) p;
            String matchMethod = permission.getMethod();
            if("*".equals(matchMethod) && serviceName.equals(permission.getServiceName())){
                return permission;
            }
            if(matchMethod.equals(method) && serviceName.equals(permission.getServiceName())){
                return permission;
            }
        }
        return null;
    }

    /***
     * 匹配方法:完全匹配
     */
    public Permission match1(List<Permission> permissionsMatch1,String uri,String method,String serviceName){
        for (Permission permission : permissionsMatch1) {
            String matchUrl = permission.getUrl();
            String matchMethod = permission.getMethod();
            if(matchUrl(matchUrl,uri)){
                //提交方式和服务匹配
                if(matchMethod.equals(method) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
                if("*".equals(matchMethod) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "member/u/{id}";
        PathMatcher matcher = new AntPathMatcher();
        System.out.println(matcher.match(url, "member/u/123"));
    }

    public boolean matchUrl(String patternUrl, String requestUrl){
        if (StringUtils.isEmpty(patternUrl) || StringUtils.isEmpty(requestUrl)) {
            return false;
        }
        PathMatcher matcher = new AntPathMatcher();
        return matcher.match(patternUrl, requestUrl);
    }

    /*****
     * 令牌解析
     */
    public Map<String,Object> jwtVerify(String token,String clientIp){
        try {
            //解析Token
            Map<String, Object> dataMap = JwtToken.parseToken(token);

            if(dataMap!=null){
                //获取用户token
                AuthMember member = JSON.parseObject(dataMap.get(SysConstant.SESSION_MEMBER).toString(),AuthMember.class);
                String key = SysConstant.TOKEN_MEMBER+ member.getId();
                Object rToke = redisTemplate.boundValueOps(key).get();
                if(rToke!=null && token.equals(rToke.toString())){
                    //获取Token中IP的MD5
//                    String ip = dataMap.get("ip").toString();
                    //比较Token中IP的MD5值和用户的IPMD5值 ip有问题
//                    if(ip.equals(MD5.md5(clientIp))){
                        //刷新token 过期时间
                        redisTemplate.expire(key,7, TimeUnit.DAYS);
                        return dataMap;
//                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
