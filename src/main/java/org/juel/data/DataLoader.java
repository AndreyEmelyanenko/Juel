package org.juel.data;

import org.juel.data.model.Serial;
import org.juel.model.SerialMeta;
import org.juel.repositories.TimeSeriesMetaRepository;
import org.juel.repositories.TimeSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final TimeSeriesRepository<Serial> timeSeriesRepository;
    private final TimeSeriesMetaRepository timeSeriesMetaRepository;
    private final BiFunction<SerialMeta, LocalDate, List<Serial>> dataExchanger;
    private final LocalDate fromLoadPeriod;

    @Autowired
    public DataLoader(TimeSeriesRepository<Serial> timeSeriesRepository,
                      TimeSeriesMetaRepository timeSeriesMetaRepository,
                      DataExchanger dataExchanger,
                      @Value("${data.period.days:365}") Integer daysToLoad) {
        this.timeSeriesRepository = timeSeriesRepository;
        this.timeSeriesMetaRepository = timeSeriesMetaRepository;
        this.dataExchanger = dataExchanger;
        this.fromLoadPeriod = LocalDate
                .now()
                .minusDays(daysToLoad);
    }

    public void processData() {
        timeSeriesMetaRepository
                .findAll()
                .stream()
                .filter(SerialMeta::getEnable)
                .peek(serialMeta -> logger.info("Try to load Serial {}", serialMeta))
                .forEach(serial -> timeSeriesRepository
                        .findLast(serial.getSign())
                        .map(existingSerial -> {
                            if (existingSerial.getForDate().isBefore(LocalDate.now())) {
                                dataExchanger
                                        .apply(serial, existingSerial.getForDate().plusDays(1))
                                        .forEach(newSerial -> timeSeriesRepository.putSerial(newSerial.getSign(), newSerial));
                            }
                            return serial;
                        })
                        .orElseGet(() -> {
                            dataExchanger
                                    .apply(serial, fromLoadPeriod)
                                    .forEach(newSerial -> timeSeriesRepository.putSerial(newSerial.getSign(), newSerial));
                            return serial;
                        }));
    }

}
