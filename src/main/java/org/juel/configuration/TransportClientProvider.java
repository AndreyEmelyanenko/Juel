package org.juel.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TransportClientProvider {

    private static final Logger log = LoggerFactory.getLogger(TransportClientProvider.class);

    @Value("${elastic.host:127.0.0.1}")
    private String elasticHost;

    @Value("${elastic.port:9200}")
    private Integer elasticPort;

    @Value("${cluster.name}")
    private String clusterName;

    private RestHighLevelClient client;

    @PostConstruct
    public void initClient() {

        try {
            this.client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(elasticHost, elasticPort, "http")));
        } catch (Exception e) {
            log.error("Got error", e);
            throw new RuntimeException(e);
        }
    }

    public RestHighLevelClient get() {
        return client;
    }

}