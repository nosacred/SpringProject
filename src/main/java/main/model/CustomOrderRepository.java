package main.model;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomOrderRepository extends CrudRepository<Order, Integer> {
    List<Order> findOrderByDateBetweenOrderByDate(ZonedDateTime zonedDateTime1, ZonedDateTime zonedDateTime2);


}
