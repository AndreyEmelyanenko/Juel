package org.juel.web.controller;


import org.juel.web.dto.ResponseSupportedSeriasDto;
import org.juel.web.dto.SerialDto;
import org.juel.web.service.SerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {

    private final SerialService serialService;

    @Autowired
    public AnalyticsController(SerialService serialService) {
        this.serialService = serialService;
    }

    @GetMapping("/series")
    public Collection<ResponseSupportedSeriasDto> getSeries(
            @RequestParam(value = "fitted", required = false) boolean isFitted) {
        return serialService.getSupportedSeries(isFitted);
    }

    @GetMapping("/series/{sign}/historical")
    public List<SerialDto> getHistoricalData(@PathVariable("sign") String sign) {
        return serialService.getHistoricalData(sign);
    }

    @GetMapping("/series/{sign}/predict")
    public List<SerialDto> getPredictForSeria(
            @PathVariable(value = "sign", required = true) String sign,
            @RequestParam(value = "days", required = false) int days) {
        return serialService.getPredictForSerial(sign, days);
    }

}
