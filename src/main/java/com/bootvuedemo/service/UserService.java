package com.bootvuedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bootvuedemo.common.util.RspResult;
import com.bootvuedemo.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Planet
 * @since 2022-02-24
 */
public interface UserService extends IService<User> {

    RspResult excelImport(MultipartFile excelFile);

    void excelExport(User user);

    Integer inserts(List<User> list);
}
