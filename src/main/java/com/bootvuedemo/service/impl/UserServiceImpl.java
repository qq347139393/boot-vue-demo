package com.bootvuedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvuedemo.dao.mapper.UserMapper;
import com.bootvuedemo.entity.User;
import com.bootvuedemo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Planet
 * @since 2022-02-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
