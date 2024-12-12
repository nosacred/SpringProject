package org.example.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StocksRepository extends CrudRepository<Stock, Integer> {

    List<Stock> findAllByApiKey(String api);
    void deleteAllByApiKey(String api);
}
