package com.bitisan.admin.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    public String index(Model model) {
        return "redirect:/login";
    }
}
