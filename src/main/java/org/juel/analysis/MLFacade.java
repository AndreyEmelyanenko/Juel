package org.juel.analysis;

import org.juel.data.model.Serial;

import java.util.List;
import java.util.Map;


public interface MLFacade {

    void fit(List<Serial> goal, Map<String, List<Serial>> data);

    List<Serial> predict(Map<String, List<Serial>> data);

    List<Serial> getGoals();

    List<String> getLabels();

    String getSign();

    Map<String, List<Serial>> getInputData();

    String serialize();

    Double getScore();

    boolean isFitted();

}
