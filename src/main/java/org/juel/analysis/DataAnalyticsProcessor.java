package org.juel.analysis;


import javafx.util.Pair;
import org.juel.data.model.Serial;
import org.juel.model.GoalMeta;
import org.juel.model.PredictiveModel;
import org.juel.repositories.GoalMetaRepository;
import org.juel.repositories.PredictiveModelRepository;
import org.juel.repositories.TimeSeriesMetaRepository;
import org.juel.repositories.TimeSeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataAnalyticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyticsProcessor.class);

    private final MLFacadeFactory mlFacadeFactory;
    private final GoalMetaRepository goalMetaRepository;
    private final TimeSeriesMetaRepository timeSeriesMetaRepository;
    private final TimeSeriesRepository<Serial> timeSeriesRepository;
    private final PredictiveModelRepository predictiveModelRepository;

    @Autowired
    public DataAnalyticsProcessor(MLFacadeFactory mlFacadeFactory,
                                  GoalMetaRepository goalMetaRepository,
                                  TimeSeriesMetaRepository timeSeriesMetaRepository,
                                  TimeSeriesRepository<Serial> timeSeriesRepository,
                                  PredictiveModelRepository predictiveModelRepository) {
        this.mlFacadeFactory = mlFacadeFactory;
        this.goalMetaRepository = goalMetaRepository;
        this.timeSeriesMetaRepository = timeSeriesMetaRepository;
        this.timeSeriesRepository = timeSeriesRepository;
        this.predictiveModelRepository = predictiveModelRepository;
    }

    void processData() {

        LOGGER.info("Trying to process analyze data");

        goalMetaRepository

                .findAll()

                .stream()

                .filter(GoalMeta::isEnabled)

                //TODO: дбавить филтр по уже недавно отработанным сериям
                .sorted(Comparator.comparingInt(GoalMeta::getPriority))

                .findFirst()

                .map(goalSeria -> {

                    LOGGER.info("Processed seria {}", goalSeria);

                    Map<String, List<Serial>> allSeriesWithoutGoal = timeSeriesMetaRepository
                            .findAll()
                            .stream()
                            .filter(serial -> !serial.getSign().equals(goalSeria.getSign().getSign()))
                            .flatMap(serialMeta -> timeSeriesRepository
                                    .findAll(serialMeta.getSign())
                                    .stream()
                            )
                            .collect(Collectors.groupingBy(Serial::getSign));

                    LOGGER.info("Data series size {}", allSeriesWithoutGoal.size());

                    List<Serial> goal = timeSeriesRepository.findAll(goalSeria.getSign().getSign());

                    MLFacade mlFacade = mlFacadeFactory.getFacade();

                    mlFacade.fit(goal, allSeriesWithoutGoal);

                    return new Pair<>(goalSeria, mlFacade);
                })

                .filter(mlFacade -> mlFacade.getValue().isFitted())

                .map(mlFacade -> new PredictiveModel()
                        .setDate(LocalDateTime.now())
                        .setScore(mlFacade.getValue().getScore())
                        .setSign(mlFacade.getKey().getSign())
                        .setSerializedModel(mlFacade.getValue().serialize()))

                .ifPresent(predictiveModelRepository::save);
    }

}
