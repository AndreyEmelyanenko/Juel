package org.juel.analysis;


import org.juel.data.model.Serial;
import org.juel.data.model.SerialMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FactorDiscarder {

    private final float enoughCorrelation;

    @Autowired
    public FactorDiscarder() {
        this.enoughCorrelation = Math.abs(0.5F);
    }

    public List<SerialMeta> filter(SerialMeta predictive, Map<String, List<Serial>> factors) {
       factors.remove(predictive.getSign());
       return null;

    }

    private float getCorrelation(Object x1, Object x2) {
        return 0.5f;
    }

}
