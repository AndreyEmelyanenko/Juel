package org.juel.web.service;

import javafx.util.Pair;
import org.juel.analysis.MLFacade;
import org.juel.analysis.MLModelProvider;
import org.juel.data.model.Serial;
import org.juel.exception.PredictiveModelNotFoundException;
import org.juel.model.SerialMeta;
import org.juel.repositories.GoalMetaRepository;
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

@Service
public class SerialServiceImpl implements SerialService {

    private static final int MAX_DAY_PREDICT = 14;

    private final TimeSeriesMetaRepository timeSeriesMetaRepository;
    private final GoalMetaRepository goalMetaRepository;
    private final TimeSeriesRepository<Serial> timeSeriesRepository;
    private final MLModelProvider mlModelProvider;

    @Autowired
    public SerialServiceImpl(TimeSeriesMetaRepository timeSeriesMetaRepository,
                             GoalMetaRepository goalMetaRepository,
                             TimeSeriesRepository<Serial> timeSeriesRepository,
                             MLModelProvider mlModelProvider) {
        this.timeSeriesMetaRepository = timeSeriesMetaRepository;
        this.goalMetaRepository = goalMetaRepository;
        this.timeSeriesRepository = timeSeriesRepository;
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

            return goalMetaRepository
                    .findAll()
                    .stream()
                    .filter(model -> mlModelProvider.getModelForSerial(model.getSign().getSign()).isPresent())
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
    public List<SerialDto> getPredictForSerial(String sign, int daysToPredict) {

        final int days;

        if(daysToPredict > MAX_DAY_PREDICT || daysToPredict <= 0) {

            days = MAX_DAY_PREDICT;

        } else {

            days = daysToPredict;

        }

        LocalDate maxPredictDay = LocalDate.now().plusDays(days);

        LocalDate minFactorsDay = LocalDate
                .now()
                .minusDays(days);

        List<LocalDate> dates = LongStream.rangeClosed(1, days)
                .mapToObj(minFactorsDay::plusDays)
                .collect(Collectors.toList());

        MLFacade mlFacade = mlModelProvider
                .getModelForSerial(sign)
                .orElseThrow(PredictiveModelNotFoundException::new);

        Map<String, List<Serial>> data = mlFacade
                .getLabels()
                .stream()
                .map(label -> new Pair<>(label, timeSeriesRepository.findNLast(label, days)))
                .map(collections -> new Pair<>(collections.getKey(), mlFacade.getLogicAggregator().stretchSeria(dates, collections.getValue())))
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
