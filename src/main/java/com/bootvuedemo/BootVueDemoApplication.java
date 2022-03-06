package com.bootvuedemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bootvuedemo.dao.mapper")//加上你项目的dao或service所在文件位置即可
public class BootVueDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootVueDemoApplication.class, args);
    }

}
