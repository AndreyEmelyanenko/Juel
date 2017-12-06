package org.juel.data;

import org.juel.data.model.ExchangeModel;
import org.juel.data.model.Serial;
import org.juel.model.SerialMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class DataExchanger implements BiFunction<SerialMeta, LocalDate, List<Serial>> {

    private final String queryTemplate;
    private final RestTemplate restTemplate;


    public DataExchanger(@Value("${exchange.api.key}") String apiKey,
                         @Value("${exchange.host}") String exchangeHost,
                         @Value("${exchange.parametrized.uri}") String parametrizedUri) {
        this.restTemplate = new RestTemplate();
        this.queryTemplate = exchangeHost + parametrizedUri.replace("$apikey", apiKey);
    }

    @Override
    public List<Serial> apply(SerialMeta serialMeta, LocalDate from) {

        boolean isFullQuery = false;

        if(!from.isAfter(LocalDate.now().minusDays(100))) {
            isFullQuery = true;
        }

        String filledUrl = getFullUrl(isFullQuery, serialMeta);

        try {
            ExchangeModel model = restTemplate
                    .getForObject(new URI(filledUrl), ExchangeModel.class);
            return model
                    .getSeriesMap()
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        Double value = entry.getValue().getCloseValue();
                        return new Serial(serialMeta.getSign(), value, date);
                    })
                    .filter(serial -> serial.getForDate().isAfter(from) || serial.getForDate().isEqual(from))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String getFullUrl(boolean isFullQuery, SerialMeta serialMeta) {
        String signed = queryTemplate
                .replace("$sign", serialMeta.getSign());

        if(isFullQuery) {
            return signed.replace("$size", "full");
        } else {
            return signed.replace("$size", "compact");
        }
    }


}
