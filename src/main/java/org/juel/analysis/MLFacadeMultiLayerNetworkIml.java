package org.juel.analysis;

import javafx.util.Pair;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.juel.data.model.Serial;
import org.juel.model.SerialMeta;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.time.temporal.ChronoUnit.DAYS;

// Клас реализующий интерфейс MLFacade и Serializable
// Первый интерфейс требуется для того, чтобы все остальные бизнес - слои не знали о том,
// Что это за алгоритм, тут можно применить как дерево, так и МОП, но бизнес - слой об этом знать не будет
// Интерфейс Serializable реализуется для того, чтобы данный объект можно было потом сериализовать и сохранить в базу данных
public class MLFacadeMultiLayerNetworkIml implements MLFacade, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLFacadeMultiLayerNetworkIml.class);
    private static final int SHIFT_DAYS = 14;

    private MultiLayerNetwork neuralNetwork;
    private final MLDataLogikAggregator mlDataLogikAggregator;

    private boolean isFitted = false;
    private Map<String, List<Serial>> inputData;
    private List<Serial> inputGoal;
    private List<Serial> normalizedGoals;
    private Map<String, List<Serial>> normalizedData;
    private SerialMeta serialMeta;
    private List<String> labels = new ArrayList<>();

    public MLFacadeMultiLayerNetworkIml() {
        this.mlDataLogikAggregator = new MLDataLogikAggregator();
    }

    @Override
    public void fit(List<Serial> goal, Map<String, List<Serial>> data) {

        if (!isEnoughData(goal, data)) {
            return;
        }

        /// Клонируем объекты для того, чтобы внешние потоки не могли поменять структуры
        this.inputData = (Map<String, List<Serial>>) ((HashMap<String, List<Serial>>) data).clone();
        this.inputGoal = (List<Serial>) ((ArrayList<Serial>) goal).clone();
        Pair<List<Serial>, Map<String, List<Serial>>> normalizedDataset = mlDataLogikAggregator.mapAndStretch(goal, data);
        this.normalizedGoals = normalizedDataset.getKey();
        this.normalizedData = normalizedDataset.getValue();
        this.neuralNetwork = createNetwork(normalizedGoals, normalizedData);
        this.isFitted = true;
        this.serialMeta = new SerialMeta()
                .setSign(goal.get(0).getSign())
                .setEnable(true);
    }

    @Override
    public List<Serial> predict(Map<String, List<Serial>> data) {

        if (!isFitted) {
            throw new InvalidStateException("Model is not fitted");
        }

        int size = data
                .values()
                .stream()
                .findFirst()
                .get()
                .size();

        INDArray forecstInputs = Nd4j.zeros(size, this.normalizedData.size());

        List<List<Serial>> dataMatrix = labels
                .stream()
                .filter(data::containsKey)
                .map(data::get)
                .collect(Collectors.toList());


        for (int i = 0; i < dataMatrix.get(0).size(); i++) {
            for (int j = 0; j < dataMatrix.size(); j++) {
                forecstInputs.putScalar(i, j, dataMatrix.get(j).get(i).getValue());
            }
        }

        INDArray actualOutput = neuralNetwork.output(forecstInputs);

        List<Double> predict = Arrays.stream(actualOutput
                .data()
                .asDouble())
                .boxed()
                .collect(Collectors.toList());

        List<Serial> resultSeria = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            resultSeria.add(new Serial(
                    serialMeta.getSign(),
                    predict.get(i),
                    dataMatrix.get(0).get(i).getForDate().plusDays(SHIFT_DAYS))
            );
        }

        return resultSeria;
    }

    @Override
    public List<Serial> getGoals() {
        return this.getGoals();
    }

    @Override
    public List<String> getLabels() {
        return this.labels;
    }

    @Override
    public String getSign() {
        return serialMeta.getSign();
    }

    public Map<String, List<Serial>> getInputData() {
        return inputData;
    }

    @Override
    public String serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            LOGGER.error("Error while serialize object {} :", this, e);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    @Override
    public Double getScore() {
        return neuralNetwork.score();
    }

    @Override
    public boolean isFitted() {
        return isFitted;
    }

    private MultiLayerNetwork createNetwork(List<Serial> goal, Map<String, List<Serial>> data) {

        DenseLayer inputLayer = new DenseLayer.Builder()
                .nIn(data.size())
                .nOut(data.size())
                .name("Input")
                .weightInit(WeightInit.DISTRIBUTION)
                .build();

        LSTM hiddenLayer = new LSTM.Builder()
                .nIn(data.size())
                .nOut(data.size())
                .name("Hidden1")
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build();

        LSTM hidden2Layer = new LSTM.Builder()
                .nIn(data.size())
                .nOut(data.size())
                .name("Hidden2")
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build();

        LSTM hidden3Layer = new LSTM.Builder()
                .nIn(data.size())
                .nOut(data.size())
                .name("Hidden3")
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build();

        OutputLayer outputLayer = new OutputLayer.Builder()
                .nIn(data.size())
                .nOut(1)
                .name("Output")
                .activation(Activation.IDENTITY)
                .weightInit(WeightInit.XAVIER)
                .lossFunction(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR)
                .build();

        NeuralNetConfiguration.Builder nncBuilder = new NeuralNetConfiguration.Builder();
        nncBuilder.iterations(10000);
        nncBuilder.learningRate(0.06);
        nncBuilder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

        NeuralNetConfiguration.ListBuilder listBuilder = nncBuilder.list();
        listBuilder.layer(0, inputLayer);
        listBuilder.layer(1, hiddenLayer);
        listBuilder.layer(2, hidden2Layer);
        listBuilder.layer(3, hidden3Layer);
        listBuilder.layer(4, outputLayer);

        listBuilder.backprop(true);

        MultiLayerNetwork myNetwork = new MultiLayerNetwork(listBuilder.build());
        myNetwork.init();

        INDArray trainingInputs = Nd4j.zeros(goal.size() + 1, inputLayer.getNIn());
        INDArray trainingOutputs = Nd4j.zeros(goal.size() + 1, outputLayer.getNOut());

        List<List<Serial>> dataMatrix = data
                .values()
                .stream()
                .peek(serials -> labels.add(serials.get(0).getSign()))
                .collect(Collectors.toList());

        for (int i = 0; i < dataMatrix.get(0).size(); i++) {
            for (int j = 0; j < dataMatrix.size(); j++) {
                trainingInputs.putScalar(i, j, dataMatrix.get(j).get(i).getValue());
            }
        }

        for (int i = 0; i < goal.size(); i++) {
            trainingOutputs.putScalar(i, 0, goal.get(i).getValue());
        }

        DataSet myData = new org.nd4j.linalg.dataset.DataSet(trainingInputs, trainingOutputs);

        myNetwork.fit(myData);

        return myNetwork;

    }

    // Простая валидация размеров данных, необходимых для обучения
    // todo: Думаю, тут требуется более умная валидация
    private boolean isEnoughData(List<Serial> goal, Map<String, List<Serial>> data) {

        if (data.size() < 1) {
            return false;
        }

        if (goal.isEmpty() || data.isEmpty()) {
            return false;
        }

        return true;
    }

    public static class MLDataLogikAggregator {

        private Pair<List<Serial>, Map<String, List<Serial>>> mapAndStretch(List<Serial> goal, Map<String, List<Serial>> data) {

            LocalDate maxDate = goal
                    .stream()
                    .map(Serial::getForDate)
                    .max(LocalDate::compareTo)
                    .get();

            LocalDate minDate = goal
                    .stream()
                    .map(Serial::getForDate)
                    .min(LocalDate::compareTo)
                    .get();

            List<LocalDate> trainingPeriud = getLocalDate(minDate, maxDate);

            List<Serial> stretchedGoals = stretchSeria(trainingPeriud, goal);

            List<Serial> stretchedShiftedGoals = stretchedGoals
                    .stream()
                    .filter(serial -> serial
                            .getForDate()
                            .isBefore(maxDate
                                    .minusDays(SHIFT_DAYS)))
                    .collect(Collectors.toList());

            Map<String, List<Serial>> stretchedData = data
                    .values()
                    .stream()
                    .flatMap(serial -> stretchSeria(trainingPeriud, serial).stream())
                    .filter(serial -> serial.getForDate().isAfter(minDate.plusDays(SHIFT_DAYS)))
                    .collect(Collectors.groupingBy(Serial::getSign));

            stretchedData.put(goal.get(0).getSign(), stretchedShiftedGoals);

            List<Serial> stretchedShiftedUpGoals = stretchedGoals
                    .stream()
                    .filter(serial -> serial.getForDate().isAfter(minDate.plusDays(SHIFT_DAYS)))
                    .collect(Collectors.toList());

            return new Pair<>(stretchedShiftedUpGoals, stretchedData);

        }


        public List<Serial> stretchSeria(List<LocalDate> dates, List<Serial> serial) {

            Map<LocalDate, Serial> serialDateMap = serial
                    .stream()
                    .collect(Collectors.toMap(Serial::getForDate, Function.identity()));

            return dates
                    .stream()
                    .map(date -> {

                        Serial seria = serialDateMap.get(date);

                        if (seria == null) {
                            seria = getSerialBetweenPoints(serialDateMap, date);
                        }
                        return seria;
                    }).collect(Collectors.toList());
        }

        private List<LocalDate> getLocalDate(LocalDate minDate, LocalDate maxDate) {

            final long days = minDate.until(maxDate, DAYS);

            return LongStream.rangeClosed(0, days)
                    .mapToObj(minDate::plusDays)
                    .collect(Collectors.toList());
        }

        private Serial getSerialBetweenPoints(Map<LocalDate, Serial> serialDateMap,
                                              LocalDate forDate) {
            LocalDate maxDate = serialDateMap
                    .keySet()
                    .stream()
                    .max(LocalDate::compareTo)
                    .get();

            LocalDate minDate = serialDateMap
                    .keySet()
                    .stream()
                    .min(LocalDate::compareTo)
                    .get();

            if (forDate.isBefore(maxDate) && forDate.isAfter(minDate)) {
                return getSerialInInterval(serialDateMap, forDate);
            } else if (forDate.isAfter(maxDate)) {

                Serial serial = serialDateMap.get(maxDate);

                return new Serial(
                        serial.getSign(),
                        serial.getValue(),
                        forDate
                );
            } else if (forDate.isBefore(minDate)) {

                Serial serial = serialDateMap.get(minDate);

                return new Serial(
                        serial.getSign(),
                        serial.getValue(),
                        forDate
                );
            }
            return null;
        }


        private Serial getSerialInInterval(Map<LocalDate, Serial> serialDateMap,
                                           LocalDate forDate) {
            Serial maxSerial = null;
            Serial minSerial = null;

            for (int i = 1; i <= serialDateMap.size() / 2; i++) {

                Serial maxSerialTmp = serialDateMap.get(forDate.plusDays(i));
                Serial minSerialTmp = serialDateMap.get(forDate.minusDays(i));

                if (maxSerial == null && maxSerialTmp != null) {
                    maxSerial = maxSerialTmp;
                }

                if (minSerial == null && minSerialTmp != null) {
                    minSerial = minSerialTmp;
                }

                if (maxSerial != null && minSerial != null) {
                    return new Serial(
                            minSerial.getSign(),
                            getAverage(maxSerial, minSerial, forDate),
                            forDate
                    );
                }

            }
            return null;
        }

        private double getAverage(Serial maxSerial, Serial minSerial, LocalDate forDate) {
            long dayToMax = DAYS.between(forDate, maxSerial.getForDate());
            long dayToMin = DAYS.between(forDate, minSerial.getForDate());
            long sumOfDay = dayToMax + dayToMin;
            return (dayToMax / sumOfDay) * maxSerial.getValue() + (dayToMin / sumOfDay) * minSerial.getValue();
        }
    }

}
