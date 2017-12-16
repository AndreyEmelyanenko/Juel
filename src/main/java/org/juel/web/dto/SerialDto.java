package org.juel.web.dto;

import java.time.LocalDate;

public class SerialDto {

    private String sign;
    private Double value;
    private LocalDate forDate;

    public String getSign() {
        return sign;
    }

    public SerialDto setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public SerialDto setValue(Double value) {
        this.value = value;
        return this;
    }

    public LocalDate getForDate() {
        return forDate;
    }

    public SerialDto setForDate(LocalDate forDate) {
        this.forDate = forDate;
        return this;
    }
}
