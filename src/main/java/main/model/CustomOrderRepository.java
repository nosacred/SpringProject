package main.model;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface CustomOrderRepository extends CrudRepository<Order, Integer> {
//    List<Order> getdOrderByApiKeyAndDateBetweenOrderByDate(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);
    List<Order> getOrderByApiKeyAndDateBetweenOrderByDate(String api, ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);


}
