package org.juel.analysis;

import org.deeplearning4j.nn.api.NeuralNetwork;
import org.juel.data.model.Serial;
import org.nd4j.linalg.api.buffer.DoubleBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// Клас реализующий интерфейс MLFacade и Serializable
// Первый интерфейс требуется для того, чтобы все остальные бизнес - слои не знали о том,
// Что это за алгоритм, тут можно применить как дерево, так и МОП, но бизнес - слой об этом знать не будет
// Интерфейс Serializable реализуется для того, чтобы данный объект можно было потом сериализовать и сохранить в базу данных
public class MLFacadeMultiLayerNetworkIml implements MLFacade, Serializable {

    private final NeuralNetwork neuralNetwork;

    public MLFacadeMultiLayerNetworkIml(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

    @Override
    public void fit(List<Serial> goal, Map<String, List<Serial>> data) {

        DataSet multiDataSet = new org.nd4j.linalg.dataset.DataSet();

        INDArray [] objects = (INDArray []) data
                .values()
                .stream()
                .map(feat -> feat
                        .stream()
                        .map(Serial::getValue)
                        .mapToDouble(x -> x)
                        .toArray())
                .map(DoubleBuffer::new)
                .map(buffer -> {
                    INDArray juelArray = new JuelArray();
                    juelArray.setData(buffer);
                    return juelArray;
                })
                .toArray();



        double[] labels = goal
                .stream()
                .map(Serial::getValue)
                .mapToDouble(y -> y)
                .toArray();

        INDArray features = new JuelArray();

        for(int i =0; i < objects.length; i++) {
            features.put(i, objects[i]);
        }

       INDArray juelArray = new JuelArray();
        juelArray.setData(new DoubleBuffer(labels));
        multiDataSet.setFeatures(features);
        multiDataSet.setLabels(juelArray);

        neuralNetwork.fit(multiDataSet);

    }

    @Override
    public List<Serial> predict(Map<String, List<Serial>> data) {
        return null;
    }
}
