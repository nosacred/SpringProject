package org.example.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomStocksRepository extends CrudRepository<Stock,StockId> {
    List<Stock> findAllByBarcode(String barcode);
    List<Stock> findAllByApiKey(String api);
    void deleteAllByApiKey(String api);
}
