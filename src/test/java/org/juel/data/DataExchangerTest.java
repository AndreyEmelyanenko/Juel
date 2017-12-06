package org.juel.data;

import org.juel.model.SerialMeta;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DataExchangerTest {
    @Test
    public void apply() throws Exception {
        DataExchanger dataExchanger = new DataExchanger(
                "N5FESE516174AMNQ",
                "https://www.alphavantage.co",
                "/query?function=TIME_SERIES_DAILY&symbol=$sign&apikey=$apikey&outsize=$size"
        );

        SerialMeta serialMeta = new SerialMeta().setEnable(true).setSign("oil");

        dataExchanger.apply(serialMeta, LocalDate.now().minusDays(50));

    }

}