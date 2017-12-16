package org.juel.analysis;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.juel.data.model.Serial;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MLFacadeMultiLayerNetworkImlTest {

    private Random random = new Random();

    @Test
    public  void testNeuralNetwork() {
        DenseLayer inputLayer = new DenseLayer.Builder()
                .nIn(2)
                .nOut(3)
                .name("Input")
                .weightInit(WeightInit.DISTRIBUTION)
                .build();

        DenseLayer hiddenLayer = new DenseLayer.Builder()
                .nIn(3)
                .nOut(3)
                .name("Hidden")
                .activation(Activation.IDENTITY)
                .weightInit(WeightInit.DISTRIBUTION)
                .build();

        OutputLayer outputLayer = new OutputLayer.Builder()
                .nIn(3)
                .nOut(1)
                .name("Output")
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.DISTRIBUTION)
                .lossFunction(LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR)
                .build();

        NeuralNetConfiguration.Builder nncBuilder = new NeuralNetConfiguration.Builder();
        nncBuilder.iterations(10000);
        nncBuilder.learningRate(0.01);
        nncBuilder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

        NeuralNetConfiguration.ListBuilder listBuilder = nncBuilder.list();
        listBuilder.layer(0, inputLayer);
        listBuilder.layer(1, hiddenLayer);
        listBuilder.layer(2, outputLayer);

        listBuilder.backprop(true);

        MultiLayerNetwork myNetwork = new MultiLayerNetwork(listBuilder.build());
        myNetwork.init();

        INDArray trainingInputs = Nd4j.zeros(4, inputLayer.getNIn());
        INDArray trainingOutputs = Nd4j.zeros(4, outputLayer.getNOut());

        // If 0,0 show 0
        trainingInputs.putScalar(new int[]{0,0}, 0);
        trainingInputs.putScalar(new int[]{0,1}, 0);
        trainingOutputs.putScalar(new int[]{0,0}, 0);

        // If 0,1 show 1
        trainingInputs.putScalar(new int[]{1,0}, 0);
        trainingInputs.putScalar(new int[]{1,1}, 1);
        trainingOutputs.putScalar(new int[]{1,0}, 1);

        // If 1,0 show 1
        trainingInputs.putScalar(new int[]{2,0}, 1);
        trainingInputs.putScalar(new int[]{2,1}, 0);
        trainingOutputs.putScalar(new int[]{2,0}, 1);

        // If 1,1 show 0
        trainingInputs.putScalar(new int[]{3,0}, 1);
        trainingInputs.putScalar(new int[]{3,1}, 1);
        trainingOutputs.putScalar(new int[]{3,0}, 0);

        DataSet myData = new org.nd4j.linalg.dataset.DataSet(trainingInputs, trainingOutputs);
        myNetwork.fit(myData);


        INDArray actualInput = Nd4j.zeros(1,2);
        actualInput.putScalar(new int[]{0,0}, 0);
        actualInput.putScalar(new int[]{0,1}, 0);

        INDArray actualOutput = myNetwork.output(actualInput);
        System.out.println("myNetwork Output " + actualOutput);
        //Output is producing 1.00. Should be 0.0
    }

    @Test
    public void testMLFacade() {

        MLFacade mlFacade = new MLFacadeMultiLayerNetworkIml();

        List<LocalDate> list = IntStream.range(0, 200)
                .boxed()
                .map(i -> LocalDate.now().minusDays(i))
                .collect(Collectors.toList());

        List<Serial> goals = list
                .stream()
                .map(date -> new Serial(
                        "goal",
                        random
                        .doubles(5d, 20d)
                        .findFirst()
                        .getAsDouble(),
                        date))
                .collect(Collectors.toList());

        List<Serial> x1 = goals
                .stream()
                .map(goal -> new Serial(
                        "x1",
                        goal.getValue() + random
                                .doubles(0.5d, 1d)
                                .findFirst()
                                .getAsDouble(),
                        goal.getForDate()))
                .collect(Collectors.toList());
/*
        List<Serial> x2 = list
                .stream()
                .map(date -> new Serial(
                        "x2",
                        random
                                .doubles(5d, 20d)
                                .findFirst()
                                .getAsDouble(),
                        date))
                .collect(Collectors.toList());

        List<Serial> x3 = list
                .stream()
                .map(date -> new Serial(
                        "x3",
                        random
                                .doubles(5d, 20d)
                                .findFirst()
                                .getAsDouble(),
                        date))
                .collect(Collectors.toList());*/

        Map<String, List<Serial>> serialArray = new LinkedHashMap<>();

        serialArray.put("x1", x1);


        mlFacade.fit(goals, serialArray);

        mlFacade.predict(serialArray);
    }





}