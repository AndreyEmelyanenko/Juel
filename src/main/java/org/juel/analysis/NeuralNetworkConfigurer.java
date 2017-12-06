package org.juel.analysis;

import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class NeuralNetworkConfigurer {

/*
    @Bean
    public MultiLayerConfiguration getNeuralNetworkConf () {

        return new NeuralNetConfiguration.Builder()
                .iterations(5)
                .weightInit(WeightInit.XAVIER)
                .regularization(true)
                .l2(0.05)
                .activation(Activation.RELU)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.05)
                .list()
                .backprop(true)
                .build();
    }

    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public NeuralNetwork getNeuralNetwork(MultiLayerConfiguration configuration) {
        return new MultiLayerNetwork(configuration);
    }

    @Bean("mlFacade")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MLFacade getMlFacade(NeuralNetwork network) {
        return new MLFacadeMultiLayerNetworkIml(network);
    }
*/

}
