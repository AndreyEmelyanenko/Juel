package org.juel.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Serial {

    private String sign;
    private Double value;
    private LocalDate forDate;

    public Serial() {
    }

    public Serial(String sign, Double value, LocalDate forDate) {
        this.sign = sign;
        this.value = value;
        this.forDate = forDate;
    }

    public Double getValue() {
        return value;
    }

    @JsonProperty
    public LocalDate getForDate() {
        return forDate;
    }

    public String getSign() {
        return sign;
    }
}
