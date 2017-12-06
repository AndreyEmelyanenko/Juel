package org.juel.data.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeValues {

    @JsonProperty("4. close")
    private Double closeValue;

    public Double getCloseValue() {
        return closeValue;
    }

    public ExchangeValues setCloseValue(Double closeValue) {
        this.closeValue = closeValue;
        return this;
    }
}
