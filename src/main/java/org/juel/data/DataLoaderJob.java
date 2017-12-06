package org.juel.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class DataLoaderJob {

    private final DataLoader dataLoader;

    @Autowired
    public DataLoaderJob(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Scheduled(fixedDelay = 3_600_600)
    public void dataRefreshJob() {
        dataLoader.processData();
    }

}
