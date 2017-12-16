package org.juel.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSeriesRepository<T> {

    String putSerial(String key, T value);

    Optional<T> findSerial(String key, LocalDate dueDate);

    Optional<T> findLast(String key);

    List<T> findNLast(String key, int count);

    List<T> findAll(String key);

}
