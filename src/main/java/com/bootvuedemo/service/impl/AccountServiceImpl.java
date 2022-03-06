package com.bootvuedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.dao.mapper.UserMapper;
import com.bootvuedemo.entity.Menu;
import com.bootvuedemo.entity.User;
import com.bootvuedemo.service.AccountService;
import com.bootvuedemo.system.MySessionContext;
import com.bootvuedemo.util.JwtUtil;
import com.bootvuedemo.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public RspResult login(User user) {
        User one = userMapper.selectOne(new QueryWrapper<User>().eq("name", user.getName()));
        if(one==null){//用户名不存在
            return RspResult.FAILED;
        }
        if(one.getPassword().equals(user.getPassword())){
            Long userId=one.getId();
            String name = one.getName();
            //存入session缓存
            HttpSession session=WebUtil.getSession();
            session.setAttribute("userId",userId);
            session.setAttribute("name",name);
            MySessionContext.AddSession(session);//存入我们自定义的专门存放sessionId和session的集合中当缓存

            //构建jwtToken
            Map<String,Object> claims = new HashMap<>();
            claims.put("userId",userId);
            claims.put("name",name);
            String sessionId = session.getId();
            log.info("sessionId="+sessionId);
            String jwtToken = JwtUtil.createJwt("auth", 3600000l,
                    sessionId, claims);

            //构建动态路由列表(这个其实是在数据库查出来并构建树形结构的,这里我们只是做个基本的演示)
            List<Menu> menus = new ArrayList<>();
            Menu m = new Menu();
            m.setIcon("el-icon-setting");
            m.setIndex("/home");
            m.setTitle("首页");
            m.setHidden(false);
            menus.add(m);

            Menu m3 = new Menu();
            m3.setIndex("/users");
            m3.setTitle("用户列表");
            m.setHidden(false);

            Menu m2 = new Menu();
            m2.setIcon("el-icon-menu");
            m2.setIndex("2");
            m2.setTitle("用户管理");
            m.setHidden(false);
            m2.setChildren(Arrays.asList(m3));
            menus.add(m2);

            //将jwtToken和动态路由列表都返回给前端
            Map<String,Object> data=new HashMap<>();
            data.put("jwtToken",jwtToken);
            data.put("menus",menus);

            return new RspResult(data);//登陆成功,返回jwtToken和动态路由列表
        }
        return RspResult.FAILED;
    }

    @Override
    public RspResult checkToken(User user) {
        //先判断jwtToken是否合法
        boolean b = JwtUtil.checkJwt(user.getJwtToken());
        if(b){
            //再判断用户的name是否真实存在..以防用户盗取他人的jwtToken放到自己的浏览器上冒充
            user = userMapper.selectOne(new QueryWrapper<User>().eq("name", user.getName()));
            if(user!=null){
                return RspResult.SUCCESS;
            }
        }
        return RspResult.FAILED;
    }


}
