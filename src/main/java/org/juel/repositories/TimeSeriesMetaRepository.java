package org.juel.repositories;

import org.juel.model.SerialMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TimeSeriesMetaRepository extends CrudRepository<SerialMeta, String> {

    SerialMeta save(String sign);
    SerialMeta findOne(String sign);
    List<SerialMeta> findAll();

}
