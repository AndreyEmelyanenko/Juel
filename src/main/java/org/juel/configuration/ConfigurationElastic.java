package org.juel.configuration;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ConfigurationElastic {

    @Value("${elastic.host:127.0.0.1}")
    private String elasticHost;

    @Value("${elastic.port:9300}")
    private Integer elasticPort;

    @Bean
    public TransportClient getElasticClient() throws UnknownHostException {
        return new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(elasticHost), elasticPort));

    }

}
