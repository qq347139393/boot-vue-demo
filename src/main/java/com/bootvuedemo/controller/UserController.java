package com.bootvuedemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.entity.User;
import com.bootvuedemo.service.UserService;
import com.bootvuedemo.util.mapAndEntityConvert.MapAndEntityConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Planet
 * @since 2022-02-24
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public RspResult inserts(@RequestBody List<User> list){
        if(list==null||list.size()==0){
            return RspResult.FAILED;
        }
        if(userService.saveBatch(list)){
            return RspResult.SUCCESS;
        }

        return RspResult.FAILED;
    }

    @RequestMapping(value = "/",method = RequestMethod.DELETE)
    public RspResult deletesByIds(@RequestParam List<Long> ids){
        if(ids==null||ids.size()==0){
            return RspResult.FAILED;
        }
        for (Long id : ids) {
            if(id.equals(1l)){
                return RspResult.SUPER_PROHIBIT;
            }
        }
        if(userService.removeByIds(ids)){
            return RspResult.SUCCESS;
        }

        return RspResult.FAILED;
    }

    @RequestMapping(value = "/",method = RequestMethod.PUT)
    public RspResult updatesByIds(@RequestBody List<User> list){
        if(list==null||list.size()==0){
            return RspResult.FAILED;
        }
        for (User user : list) {
            if(user.getId().equals(1l)){
                return RspResult.SUPER_PROHIBIT;
            }
        }
        if(userService.updateBatchById(list)){
            return RspResult.SUCCESS;
        }

        return RspResult.FAILED;
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public RspResult selectsByIds(@RequestParam List<Long> ids){
        if(ids==null||ids.size()==0){
            return RspResult.FAILED;
        }
        List<User> users = (List<User>)userService.listByIds(ids);

        return new RspResult(users);
    }

    @RequestMapping(value = "byPage",method = RequestMethod.POST)
    public RspResult selectsByPage(@RequestBody User user){
        if(user==null){
            return RspResult.FAILED;
        }
        Page<User> page = new Page(user.getCurrent(), user.getSize());
        //这里先做出基本的属性遍历查询,以后要加入模糊、大小等各种可能出现的情况的判断方式
        QueryWrapper<User> tQueryWrapper = new QueryWrapper<>();
        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = MapAndEntityConvertUtil.entityToMap(user, 0,true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return RspResult.SYS_ERROR;
        }

        //将需要作为查询条件的字段装配到QueryWrapper上,用于拼装查询sql(目前我们只做等于这种,以后会改进成各种方式的)
        for (String key : stringObjectMap.keySet()) {
            if("size".equals(key)||"current".equals(key)){
                continue;
            }
            if("key".equals(key)){
                //标准模糊查询
                //tQueryWrapper.like("name", name).or().like("lastname", name)
                tQueryWrapper.like("name", stringObjectMap.get("key"));
                continue;
            }
            tQueryWrapper.eq(key,stringObjectMap.get(key));
        }

        IPage<User> pageData = userService.page(page, tQueryWrapper.orderByDesc("updatime"));
        return new RspResult(pageData);
    }


}

