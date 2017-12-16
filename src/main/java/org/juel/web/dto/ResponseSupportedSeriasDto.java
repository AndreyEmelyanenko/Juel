package org.juel.web.dto;

import javax.validation.constraints.NotNull;

public class ResponseSupportedSeriasDto {

    @NotNull
    private String serialName;

    public String getSerialName() {
        return serialName;
    }

    public ResponseSupportedSeriasDto setSerialName(String serialName) {
        this.serialName = serialName;
        return this;
    }
}
