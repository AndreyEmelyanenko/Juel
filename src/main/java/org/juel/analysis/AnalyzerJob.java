package org.juel.analysis;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyzerJob {

    private final DataAnalyticsProcessor dataAnalyticsProcessor;

    public AnalyzerJob(DataAnalyticsProcessor dataAnalyticsProcessor) {
        this.dataAnalyticsProcessor = dataAnalyticsProcessor;
    }

    @Scheduled(fixedDelay = 10_000)
    public void fit() {
        dataAnalyticsProcessor.processData();
    }

}
