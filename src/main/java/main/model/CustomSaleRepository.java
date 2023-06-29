package main.model;

import org.springframework.data.repository.CrudRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface CustomSaleRepository extends CrudRepository<Sale,String> {
//    List<Sale> getSaleByApiKeyAndDateBetweenOrderByDate(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);
    List<Sale> getSaleByApiKeyAndDateBetweenOrderByDate(String api, ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);
}
