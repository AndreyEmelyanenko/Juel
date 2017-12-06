package org.juel.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeadFormController {

    @RequestMapping("/")
    public String getHello(){
        return "HELLO !";
    }


}
