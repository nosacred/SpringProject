package main.model;

import org.springframework.data.repository.CrudRepository;

public interface StocksRepository extends CrudRepository<Stock, Integer> {
}
