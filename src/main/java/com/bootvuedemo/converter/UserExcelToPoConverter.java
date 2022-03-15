package com.bootvuedemo.converter;

import com.bootvuedemo.entity.excel.User;
import com.planetProvide.easyExcelPlus.core.baseConvert.BaseExcelToPoConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserExcelToPoConverter extends BaseExcelToPoConverter<User> {
    @Override
    public List convertExcelToPo(List<User> rows) {
        List<com.bootvuedemo.entity.User> users = rows.stream().map(row -> {
            com.bootvuedemo.entity.User user = new com.bootvuedemo.entity.User();
            user.setName(row.getName());
            user.setPassword(row.getPassword());
            user.setAge(row.getAge());
            user.setGender(row.getGender());
            user.setDescrib(row.getDescrib());
            return user;
        }).collect(Collectors.toList());

        return users;
    }

    @Override
    public List<User> convertPoToExcel(List list) {
        Object users = list.stream().map(l -> {
            com.bootvuedemo.entity.User user = (com.bootvuedemo.entity.User) l;
            User row = new User();
            row.setName(user.getName());
            row.setPassword("保密");
            row.setAge(user.getAge());
            row.setGender(user.getGender());
            row.setDescrib(user.getDescrib());
            return row;
        }).collect(Collectors.toList());

        return (List<User>)users;
    }
}
