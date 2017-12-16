package org.juel.analysis;

import org.juel.model.PredictiveModel;
import org.juel.repositories.PredictiveModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@EnableScheduling
public class MLModelProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLModelProvider.class);

    private PredictiveModelRepository predictiveModelRepository;
    private AtomicReference<Map<String, MLFacade>> mlFacadeMap = new AtomicReference<>(Collections.emptyMap());

    @Autowired
    public MLModelProvider(PredictiveModelRepository predictiveModelRepository) {
        this.predictiveModelRepository = predictiveModelRepository;
    }

    public Optional<MLFacade> getModelForSerial(String sign) {
        return Optional.ofNullable(mlFacadeMap
                .get()
                .get(sign));
    }

    @Scheduled(fixedRateString = "${refresh.model.rate:3600000}")
    public void refreshModelsList() {
        Map<String, MLFacade> mlModels = predictiveModelRepository
                .findAll()
                .stream()
                .map(this::deserializeModel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(MLFacade::getSign))
                .values()
                .stream()
                .map(models -> models
                        .stream()
                        .max(Comparator.comparingDouble(MLFacade::getScore))
                        .get())
                .collect(Collectors.toMap(MLFacade::getSign, Function.identity()));

        mlFacadeMap.set(mlModels);
    }

    private Optional<MLFacade> deserializeModel(PredictiveModel predictiveModel) {
        byte[] dec = Base64.getDecoder().decode(predictiveModel.getSerializedModel());
        InputStream inputStream = new ByteArrayInputStream(dec);
        try {
            MLFacade mlFacade = (MLFacade) new ObjectInputStream(inputStream).readObject();
            return Optional.ofNullable(mlFacade);
        } catch (Exception e) {
            LOGGER.error("Error while deserialize model", e);
            return Optional.empty();
        }
    }


}
