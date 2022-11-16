package main;

import main.model.Order;
import main.model.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping("/in")
    public  void setOrders() throws IOException {
        GetOrder.getOrdersAtDate(ZonedDateTime.now());
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
}




