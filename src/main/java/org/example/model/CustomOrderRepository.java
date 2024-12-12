package org.example.model;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CustomOrderRepository extends CrudRepository<Order, Integer> {
//    List<Order> getdOrderByApiKeyAndDateBetweenOrderByDate(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);
    List<Order> getOrderByApiKeyAndDateBetweenOrderByDate(String api, ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);


}
