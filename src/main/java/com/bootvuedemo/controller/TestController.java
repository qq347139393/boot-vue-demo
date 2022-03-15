package com.bootvuedemo.controller;

import com.bootvuedemo.common.util.RspResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/getTest1",method = RequestMethod.GET)
    public RspResult getTest1(){
        return new RspResult("getTest1成功");
    }

    @RequestMapping(value = "/getTest2",method = RequestMethod.GET)
    public RspResult getTest2(@RequestParam String test){
        System.out.println(test);
        return new RspResult("getTest2成功");
    }

    @RequestMapping(value = "/getTest3",method = RequestMethod.GET)
    public RspResult getTest3(@RequestParam(name = "test") String test){
        System.out.println(test);
        return new RspResult("getTest3成功");
    }

    @RequestMapping(value = "/getTest1",method = RequestMethod.POST)
    public RspResult postTest1(){
        return new RspResult("postTest1成功");
    }

    @RequestMapping(value = "/putTest1",method = RequestMethod.PUT)
    public RspResult putTest1(){
        return new RspResult("putTest1成功");
    }

    @RequestMapping(value = "/deleteTest1",method = RequestMethod.DELETE)
    public RspResult deleteTest1(){
        return new RspResult("deleteTest1成功");
    }

}
