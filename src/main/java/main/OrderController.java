package main;

import main.model.Order;
import main.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping("/in")
    public  void setOrders() throws IOException {
        GetOrder.getOrdersAtDate(ZonedDateTime.now().minusDays(3));
    }

    @RequestMapping("/push")
    public void pushOrders(){
        for(Order order :GetOrder.ordersArr){
            orderRepository.save(order);
        }
    }

    @RequestMapping("/show")
    public String showOrders(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Iterable<Order> orders = orderRepository.findAll();
        String responce="<ol>";
        for(Order order : orders){
           responce = responce.concat("<li>"+ order.getBrand() + " " + order.getDate().format(formatter) + " " +
                   order.getSupplierArticle())+ "</li>";
        }
        responce = responce.concat("</ol>");
        return responce;
    }

    @RequestMapping("/test")
    public String test(){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Iterable<Order> orderIterable= orderRepository.findAll();
        ArrayList<Order> orders = new ArrayList<>();
        for(Order order : orderIterable){
            orders.add(order);
        }
        LocalDate start = LocalDate.of(2022,11,14);
        LocalDate end = LocalDate.of(2022,11,17);
        OrderFunctionsController.getOrdersInPeriod(start,end,orders);
        return "TestSucces";
    }
}




