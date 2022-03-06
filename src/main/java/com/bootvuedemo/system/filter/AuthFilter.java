package com.bootvuedemo.system.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.system.MySessionContext;
import com.bootvuedemo.util.JwtUtil;
import com.bootvuedemo.util.WebUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;
//        HttpServletResponse httpServletResponse=(HttpServletResponse)response;
        String url=httpServletRequest.getServletPath();
        log.debug("url:"+url);
        if(url==null||"".equals(url)){//路径为空,返回错误信息
            authAndFunctionFailedRspBuild(response,RspResult.URL_ERROR);
            return;
        }
        //0.白名单放行
        if(whiteUrlListCheck(url)){//白名单路径,直接放行
            chain.doFilter(request,response);
            return;
        }
        //1.进行身份认证
        //判断当前请求头中是否带有jwtToken的字符串
        String jwtToken = httpServletRequest.getHeader("jwtToken");
        //如果有：走jwt校验
        if (jwtToken != null && !"".equals(jwtToken.trim()) && !"null".equalsIgnoreCase(jwtToken.trim()) && !"undefined".equalsIgnoreCase(jwtToken.trim())){
            boolean verifyToken = JwtUtil.checkJwt(jwtToken);
            if (verifyToken){
                //如果jwtToken合格,那么还要检查sessionId是否合格
                //这里不能用原生的sessionId,而是应该用属于jwtToken封装的sessionId来判断是否出现设备篡改
                Object jti = JwtUtil.decodeJwt(jwtToken).get("jti");
                String sessionId=(String)jti;
                //这里是比较,token的sessionId与浏览器设备本身带过来的sessionId是否一致:如果不一致,说明用户盗用了别的设备的token来伪装登录
//                if(!WebUtil.getSession().getId().equals(sessionId)){//设备篡改
//                    log.error("设备篡改");
//                    authAndFunctionFailedRspBuild(response,RspResult.NOLOGIN);
//                    return;
//                }
                HttpSession session = MySessionContext.getSession(sessionId);
                Object objUserId = session.getAttribute("userId");
                if(objUserId!=null){
                    Long userId=(Long)(objUserId);
                    if(userId==null){//未登录状态
                        authAndFunctionFailedRspBuild(response,RspResult.NOLOGIN);
                        return;
                    }
                }else{//未登录状态
                    authAndFunctionFailedRspBuild(response,RspResult.NOLOGIN);
                    return;
                }
            }else {
                //如果sessionId不合格,则拒绝访问
                authAndFunctionFailedRspBuild(response,RspResult.NOLOGIN);
                return;
            }
        }else{
            //如果没有jwtToken,则拒绝访问
            authAndFunctionFailedRspBuild(response,RspResult.NOLOGIN);
            return;
        }

        chain.doFilter(request,response);
    }

    /**
     * 判断当前接口url是否在白名单上
     * @param url
     * @return
     */
    private boolean whiteUrlListCheck(String url){
        if(Pattern.matches(".*\\.(js|css|gif|jpg|ico|png)$", url)||
                Pattern.matches("^/swagger.*", url)||
                Pattern.matches(".*\\.(woff2)$", url)||
                "/csrf".equals(url)||"/".equals(url)||"/v2/api-docs".equals(url)||
//                "/account/logout".equals(url)||
                "/account/login".equals(url)||
                "/account/checkToken".equals(url)||
                "/index.html".equals(url)){
            return true;
        }
        return false;
    }

    /**
     * 构建接口访问被禁止后的响应数据
     * @param response
     */
    private void authAndFunctionFailedRspBuild(ServletResponse response, RspResult rspResult) throws IOException {
        String jsonStr = JSONUtil.toJsonStr(rspResult);
//        JSONObject JSONRspResult = JSON.parseObject(jsonStr);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.append(jsonStr);
        out.println();
        out.close();
    }


}
