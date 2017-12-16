package org.juel.web.service;

import javafx.util.Pair;
import org.juel.analysis.MLFacade;
import org.juel.analysis.MLFacadeMultiLayerNetworkIml;
import org.juel.analysis.MLModelProvider;
import org.juel.data.model.Serial;
import org.juel.exception.PredictiveModelNotFoundException;
import org.juel.model.SerialMeta;
import org.juel.repositories.PredictiveModelRepository;
import org.juel.repositories.TimeSeriesMetaRepository;
import org.juel.repositories.TimeSeriesRepository;
import org.juel.web.dto.ResponseSupportedSeriasDto;
import org.juel.web.dto.SerialDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class SerialServiceImpl implements SerialService {

    private final TimeSeriesMetaRepository timeSeriesMetaRepository;
    private final PredictiveModelRepository predictiveModelRepository;
    private final TimeSeriesRepository<Serial> timeSeriesRepository;
    private final MLFacadeMultiLayerNetworkIml.MLDataLogikAggregator mlDataLogikAggregator;
    private final MLModelProvider mlModelProvider;

    @Autowired
    public SerialServiceImpl(TimeSeriesMetaRepository timeSeriesMetaRepository,
                             PredictiveModelRepository predictiveModelRepository,
                             TimeSeriesRepository<Serial> timeSeriesRepository,
                             MLModelProvider mlModelProvider) {
        this.timeSeriesMetaRepository = timeSeriesMetaRepository;
        this.predictiveModelRepository = predictiveModelRepository;
        this.timeSeriesRepository = timeSeriesRepository;
        this.mlDataLogikAggregator = new MLFacadeMultiLayerNetworkIml.MLDataLogikAggregator();
        this.mlModelProvider = mlModelProvider;
    }

    @Override
    public Collection<ResponseSupportedSeriasDto> getSupportedSeries(boolean isFitted) {

        Map<String, ResponseSupportedSeriasDto> allSupportedSeries = timeSeriesMetaRepository
                .findAll()
                .stream()
                .filter(SerialMeta::getEnable)
                .map(meta -> new ResponseSupportedSeriasDto()
                        .setSerialName(meta.getSign()))
                .collect(Collectors.toMap(ResponseSupportedSeriasDto::getSerialName, Function.identity()));

        if (isFitted) {

            return predictiveModelRepository
                    .findAll()
                    .stream()
                    .filter(model -> model.getSerializedModel() != null)
                    .filter(model -> allSupportedSeries.containsKey(model.getSign().getSign()))
                    .map(model -> allSupportedSeries.get(model.getSign().getSign()))
                    .collect(Collectors.toList());

        } else {

            return allSupportedSeries
                    .values();
        }

    }

    @Override
    public List<SerialDto> getHistoricalData(String sign) {
        return timeSeriesRepository
                .findAll(sign)
                .stream()
                .map(serialDtoMapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<SerialDto> getPredictForSerial(String sign, int days) {

        LocalDate maxPredictDay = LocalDate.now().plusDays(days);

        LocalDate minFactorsDay = LocalDate
                .now()
                .minusDays(days);

        final long daysTrainingDataset = minFactorsDay
                .until(LocalDate.now(), DAYS);

        List<LocalDate> dates = LongStream.rangeClosed(0, days)
                .mapToObj(minFactorsDay::plusDays)
                .collect(Collectors.toList());

        MLFacade mlFacade = mlModelProvider
                .getModelForSerial(sign)
                .orElseThrow(PredictiveModelNotFoundException::new);

        Map<String, List<Serial>> data = mlFacade
                .getLabels()
                .stream()
                .map(label -> new Pair<>(label, timeSeriesRepository.findNLast(label, days)))
                .map(collections -> new Pair<>(collections.getKey(), mlDataLogikAggregator.stretchSeria(dates, collections.getValue())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return mlFacade
                .predict(data)
                .stream()
                .map(serialDtoMapper)
                .filter(dto -> !dto.getForDate().isAfter(maxPredictDay))
                .collect(Collectors.toList());
    }

    private final Function<Serial, SerialDto> serialDtoMapper = serial -> new SerialDto()
            .setForDate(serial.getForDate())
            .setSign(serial.getSign())
            .setValue(serial.getValue());

}
