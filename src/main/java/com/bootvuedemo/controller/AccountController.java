package com.bootvuedemo.controller;

import cn.hutool.core.util.StrUtil;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.entity.User;
import com.bootvuedemo.service.AccountService;
import com.bootvuedemo.system.MySessionContext;
import com.bootvuedemo.util.JwtUtil;
import com.bootvuedemo.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public RspResult login(@RequestBody User user){
        if(user==null|| StrUtil.isEmpty(user.getName())||StrUtil.isEmpty(user.getPassword())){
            return RspResult.FAILED;
        }

        return accountService.login(user);
    }

    @RequestMapping(value = "/checkToken",method = RequestMethod.POST)
    public RspResult checkToken(@RequestBody User user){
        //需要name和jwtToken
        if(user==null||StrUtil.isEmpty(user.getJwtToken())||StrUtil.isEmpty(user.getName())){
            return RspResult.FAILED;
        }
        RspResult rspResult=accountService.checkToken(user);
        if(rspResult!=null){
            return rspResult;
        }
        return RspResult.FAILED;
    }

    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public RspResult logout(){
        //清除缓存
        String jwtToken = WebUtil.getRequest().getHeader("jwtToken");
        Object jti = JwtUtil.decodeJwt(jwtToken).get("jti");
        String sessionId=(String)jti;
        HttpSession session = MySessionContext.getSession(sessionId);
        //1.清楚session集合中的map
        MySessionContext.DelSession(session);
        //2.清除session缓存中的session
        session.invalidate();

        System.out.println("logout SUCCESS");
        return RspResult.SUCCESS;
    }

}
