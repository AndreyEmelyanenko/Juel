package org.juel.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.juel.data.model.Serial;
import org.junit.Test;

import java.time.LocalDate;

public class TimeSeriesElasticSearchRepositoryImplTest {


    @Test
    public void putSerial() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Serial serial = new Serial("oil", 1.4d, LocalDate.now());
        System.out.println(objectMapper.writeValueAsString(serial));
    }

}