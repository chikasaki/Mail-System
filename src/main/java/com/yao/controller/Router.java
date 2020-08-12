package com.yao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/route")
public class Router {

    @RequestMapping("/toSearch")
    public String toSearch(){
        return "query";
    }

    @RequestMapping("/toEmailInfo")
    public String toEmailInfo(){
        return "email_info";
    }
}
