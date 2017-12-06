package org.juel.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeModel {

    @JsonProperty("Meta Data")
    private Map<String, String> metaData;

    @JsonProperty("Time Series (Daily)")
    private Map<String, ExchangeValues> seriesMap;

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public ExchangeModel setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
        return this;
    }

    public Map<String, ExchangeValues> getSeriesMap() {
        return seriesMap;
    }

    public ExchangeModel setSeriesMap(Map<String, ExchangeValues> seriesMap) {
        this.seriesMap = seriesMap;
        return this;
    }
}
