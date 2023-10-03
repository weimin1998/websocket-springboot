package com.example.websocket.controller;

import com.example.websocket.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class IndexController {
    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/main")
    public String mainPage() {
        return "main";
    }

    @RequestMapping("/loginerror")
    public String longinError() {
        return "loginerror";
    }

}
