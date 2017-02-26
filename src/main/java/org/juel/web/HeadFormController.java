package org.juel.web;

import org.juel.model.UserCredential;
import org.juel.repositories.UsersCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class HeadFormController {

    @RequestMapping("/")
    public String getHello(){
        return "HELLO !";
    }


}
