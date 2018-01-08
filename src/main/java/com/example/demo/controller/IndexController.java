package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Create by ky.bai on 2018-01-06 16:28
 */
@Controller
public class IndexController {

    @RequestMapping(value = {"", "/", "/index", "/home"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    //@RequestMapping(value = "/login", method = RequestMethod.POST)
    //@ResponseBody
    //public String login(String username, String password) {
    //
    //    return "111";
    //}
}
