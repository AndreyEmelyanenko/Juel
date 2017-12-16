package org.juel.analysis;

import javafx.util.Pair;
import org.juel.data.model.Serial;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MLDataLogicAggregator {

    List<Serial> stretchSeria(List<LocalDate> dates, List<Serial> serial);

    Pair<List<Serial>, Map<String, List<Serial>>> mapAndStretch(List<Serial> goal, Map<String, List<Serial>>  data);

}
