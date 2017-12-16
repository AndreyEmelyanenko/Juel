package org.juel.repositories;

import org.juel.model.GoalMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalMetaRepository extends CrudRepository<GoalMeta, String> {

    List<GoalMeta> findAll();

    GoalMeta findOne(String sign);
}
