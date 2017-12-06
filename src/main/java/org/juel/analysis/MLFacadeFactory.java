package org.juel.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MLFacadeFactory {

    private final ApplicationContext environment;

    @Autowired
    public MLFacadeFactory(ApplicationContext environment) {
        this.environment = environment;
    }

    public MLFacade getFacade() {
        return (MLFacade) environment.getBean("mlFacade");
    }

}
