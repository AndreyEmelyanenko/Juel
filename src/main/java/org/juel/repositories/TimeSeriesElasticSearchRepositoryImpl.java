package org.juel.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.juel.configuration.TransportClientProvider;
import org.juel.data.model.Serial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Repository
public class TimeSeriesElasticSearchRepositoryImpl implements TimeSeriesRepository<Serial> {

    private static final String ELASTIC_INDEX = "time_series";
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSeriesElasticSearchRepositoryImpl.class);

    private final TransportClientProvider transportClientProvider;
    private final ObjectMapper mapper;

    @Autowired
    public TimeSeriesElasticSearchRepositoryImpl(TransportClientProvider transportClientProvider,
                                                 ObjectMapper objectMapper) {
        this.transportClientProvider = transportClientProvider;
        this.mapper = objectMapper;
    }

    @Override
    public String putSerial(String key, Serial value) {
        try {
            IndexRequest indexRequest = new IndexRequest(ELASTIC_INDEX, value.getSign(), value.getForDate().toString())
                    .source(mapper.writeValueAsString(value), XContentType.JSON);

            return transportClientProvider
                    .get()
                    .index(indexRequest)
                    .getId();
        } catch (Exception e) {
            LOGGER.error("Error while processing entity {}", value, e);
            return null;
        }
    }

    @Override
    public Optional<Serial> findSerial(String type, LocalDate dueDate) {
        GetRequest getRequest = new GetRequest(ELASTIC_INDEX, type, dueDate.toString());
        try {
            GetResponse getResponse = transportClientProvider
                    .get()
                    .get(getRequest);

            return Optional.ofNullable(mapper.readValue(getResponse.getSourceAsString(), Serial.class));

        } catch (Exception e) {
            LOGGER.error("Error while get entity {}", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Serial> findLast(String key) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(SortBuilders.fieldSort("forDate").order(SortOrder.DESC));
        searchSourceBuilder.size(1);
        SearchRequest searchRequest = new SearchRequest()
                .indices(ELASTIC_INDEX)
                .types(key)
                .source(searchSourceBuilder);
        try {
            return Stream.of(
                    transportClientProvider
                            .get()
                            .search(searchRequest)
                            .getHits()
                            .getHits())
                    .flatMap(hit -> {
                        try {
                            return Stream.of(mapper.readValue(hit.getSourceAsString(), Serial.class));
                        } catch (IOException e) {
                            LOGGER.error("Error while mapping hits to Serial", e);
                            return Stream.empty();
                        }
                    })
                    .filter(Objects::nonNull)
                    .findAny();
        } catch (Exception e) {
            LOGGER.error("Error while searching serial {}", e);
            return Optional.empty();
        }
    }

    @Override
    public List<Serial> findNLast(String key, int count) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(SortBuilders.fieldSort("forDate").order(SortOrder.DESC));
        searchSourceBuilder.size(count);
        SearchRequest searchRequest = new SearchRequest()
                .indices(ELASTIC_INDEX)
                .types(key)
                .source(searchSourceBuilder);
        try {
            return Stream.of(
                    transportClientProvider
                            .get()
                            .search(searchRequest)
                            .getHits()
                            .getHits())
                    .flatMap(hit -> {
                        try {
                            return Stream.of(mapper.readValue(hit.getSourceAsString(), Serial.class));
                        } catch (IOException e) {
                            LOGGER.error("Error while mapping hits to Serial", e);
                            return Stream.empty();
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error while searching serial {}", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Serial> findAll(String key) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort(SortBuilders.fieldSort("forDate").order(SortOrder.DESC));
        searchSourceBuilder.size(365);
        SearchRequest searchRequest = new SearchRequest(ELASTIC_INDEX);
        searchRequest.types(key);
        searchRequest.source(searchSourceBuilder);

        try {
            return Stream.of(
                    transportClientProvider
                            .get()
                            .search(searchRequest)
                            .getHits()
                            .getHits())
                    .flatMap(hit -> {
                try {
                    return Stream.of(mapper.readValue(hit.getSourceAsString(), Serial.class));
                } catch (IOException e) {
                    LOGGER.error("Error while mapping hits to Serial", e);
                    return Stream.empty();
                }
            })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error while find all {}", e);
            return Collections.emptyList();
        }
    }
}
