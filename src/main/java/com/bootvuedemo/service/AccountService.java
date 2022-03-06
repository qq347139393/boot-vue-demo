package com.bootvuedemo.service;

import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.entity.User;

public interface AccountService {

    RspResult login(User user);

    RspResult checkToken(User user);
}
