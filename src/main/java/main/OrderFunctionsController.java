package main;

import main.model.Order;
import main.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class OrderFunctionsController {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static ArrayList<Order> getOrdersInPeriod(LocalDate start, LocalDate end, ArrayList<Order> ordersIn){
        ArrayList<Order> orders = new ArrayList<>();

        while (!start.isAfter(end)) {
            for(Order order : ordersIn){
                if(order.getDate().format(formatter).equals(start.format(formatter))){
                    orders.add(order);
                }
            }

           start = start.plusDays(1);
        }
        for(Order order : orders){
            System.out.println(order.getDate());
        }
        return orders;

    }
}
