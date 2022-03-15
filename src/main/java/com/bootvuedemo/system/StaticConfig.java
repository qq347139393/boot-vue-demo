package com.bootvuedemo.system;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class StaticConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
    /**
     * /static/** 的意思是将static下的所有文件夹及相关子文件夹都添加进扫描路径
     * 修改之后重启项目就可以对static下的静态资源进行分类而且访问的时候不会出现404了
     */

}
