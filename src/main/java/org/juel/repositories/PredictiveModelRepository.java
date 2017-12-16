package org.juel.repositories;

import org.juel.model.PredictiveModel;
import org.juel.model.SerialMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PredictiveModelRepository extends CrudRepository<PredictiveModel, Long> {

    PredictiveModel save(PredictiveModel entity);

    List<PredictiveModel> findBySign(SerialMeta serialMeta);

}
