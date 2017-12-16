package org.juel.web.service;

import org.juel.web.dto.ResponseSupportedSeriasDto;
import org.juel.web.dto.SerialDto;

import java.util.Collection;
import java.util.List;

public interface SerialService {

    Collection<ResponseSupportedSeriasDto> getSupportedSeries(boolean isFitted);

    List<SerialDto> getHistoricalData(String sign);

    List<SerialDto> getPredictForSerial(String sign, int days);

}
