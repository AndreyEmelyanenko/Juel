package org.juel.analysis;


import org.juel.data.DataLoader;
import org.juel.data.model.SerialMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public class Analyst {

    private final FactorDiscarder factorDiscarder;
    private final DataLoader dataLoader;
    private final Long periodDays;
    private final MLFacadeFactory mlFacadeFactory;

    public Analyst(FactorDiscarder factorDiscarder,
                   DataLoader dataLoader,
                   @Value("${period.days:100}") Long periodDays,
                   MLFacadeFactory mlFacadeFactory
    ) {
        this.factorDiscarder = factorDiscarder;
        this.dataLoader = dataLoader;
        this.periodDays = periodDays;
        this.mlFacadeFactory = mlFacadeFactory;
    }

    public MLFacade createModel(SerialMeta predictive) {
/*
        // Преиод с которого происходит загрузка и анализ данных (сделано дял актуализации)
        LocalDate fromPeriod = LocalDate.now().minusDays(periodDays);

        // Выгрузили имеющиеся временные ряды  и метофинформацию о них
        Map<String, List<Serial>> timeSeries = dataLoader
                .getTimeSeries(fromPeriod);

        //  Достали из общего списка целевой ряд
        List<Serial> goalSerial = timeSeries
                .get(predictive.getSign());

        // Если целевой ряд не найден
        if(goalSerial == null) {
            throw new PredictiveTimeSeriesNotFoundException();
        }

        // Фильтруем имеющиеся факторы по встроенным фильтрам
        List<SerialMeta> filteredSeries = factorDiscarder.filter(
                predictive,
                timeSeries
        );

        // Сформировали map рядов
        Map<String, List<Serial>> seriesMap = filteredSeries
                .stream()
                .map(serial -> new Pair<>(serial,timeSeries.get(serial.getSign())))
                .filter(p -> p.getValue() != null)
                .collect(Collectors.toMap(p -> p.getKey().getSign(), Pair::getValue));

        // Достали из фабрики некоторый объект,
        // который реализует интерфейс MLFacade
        MLFacade mlFacade = mlFacadeFactory.getFacade();

        // Обучили данный объект
        mlFacade.fit(goalSerial, seriesMap);

        // Возвращаем обученную модель
        return mlFacade;*/
        return null;
    }

}
