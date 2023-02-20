package main.model;

import org.springframework.data.repository.CrudRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface CustomSaleRepository extends CrudRepository<Sale,String> {
    List<Sale> getSaleByDateBetweenOrderByDate (ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);
}
