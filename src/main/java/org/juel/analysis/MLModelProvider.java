package org.juel.analysis;

import org.juel.model.PredictiveModel;
import org.juel.repositories.PredictiveModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
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
                .filter(model -> model.getSerializedModel() != null)
                .map(this::deserializeModel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(MLFacade::getSign, Function.identity()));

        mlFacadeMap.set(mlModels);
    }

    private Optional<MLFacade> deserializeModel(PredictiveModel predictiveModel) {
        InputStream inputStream = new ByteArrayInputStream(predictiveModel.getSerializedModel().getBytes());
        try {
            MLFacade mlFacade = (MLFacade) new ObjectInputStream(inputStream).readObject();
            return Optional.ofNullable(mlFacade);
        } catch (Exception e) {
            LOGGER.error("Error while deserialize model", e);
            return Optional.empty();
        }
    }



}
