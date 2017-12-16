package org.juel.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeadFormController {

    @RequestMapping("/")
    public String getHello(){
        return "HELLO !";
    }


}
