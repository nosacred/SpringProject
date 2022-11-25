package main;

import main.model.Order;
import main.model.OrderRepository;
import main.model.OrderSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



@RestController
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private GetOrder getOrder;
    @Autowired
    private OrderService orderService;




    @GetMapping("/get")
    public  String setOrders(@RequestParam (value = "date",required = false, defaultValue = "2022-11-20T20:20:20") String date) throws IOException {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime ld = LocalDateTime.parse(date,formatter);
        ZonedDateTime znd = ZonedDateTime.of(ld,ZoneId.systemDefault());
        System.out.println(znd);

        try {

            getOrder.getAllOrdersAtDate(znd);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "Данные по заказам за " + ld.format(formatter)+ " получены";
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
    public String test() throws IOException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Iterable<Order> orderIterable= orderRepository.findAll();
        ArrayList<Order> orders = new ArrayList<>();
        for(Order order : orderIterable){
            orders.add(order);
        }
        LocalDate start = LocalDate.of(2022,11,14);
        LocalDate end = LocalDate.of(2022,11,17);
        HashMap<String, ArrayList<Order>> periodOrdersHashMap = orderService.getOrdersMap(orders);
        ArrayList<OrderSum> orderSums = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Order>> entry : periodOrdersHashMap.entrySet()) {
            orderService.ordersPerBarcode(entry.getValue());
            orderSums.add(new OrderSum(entry.getValue()));
        }

        Collections.sort(orderSums);
        Collections.reverse(orderSums);

        orderService.getOrdersInPeriod(start,end,orders);
        return "TestSucces";
    }

    @RequestMapping("/new")
    public String newOrdersNow() throws IOException {
        ArrayList<Order> newOrders = getOrder.getNewOrdersNow();
        String ret = "<table><tr><th>Предмет</th>" +
                "<th>Артикул поставщика </th>"+
                "<th>Размер </th>"+
                "<th>Сумма заказа </th>"+
                "<th>Склад заказа</th>"+
                "<th>Регион заказа </th>"+
                "<th>Дата заказа </th>"+
                "<th>Бренд </th></tr>";

        int i =1;
        for(Order order :newOrders){

           ret =  ret.concat("<tr>" +"<td>"+ i+"</td>"+
                   "<td>"+ order.getSubject()+"</td>"
        + "<td>"+ "<a href =\""+order.getWBLink()+"\">"+  order.getSupplierArticle()+"</a></td>"+
                   "<td>"+ order.getTechSize()+"</td>"+
                   "<th>"+ order.getTotalPriceWithDisc()+"</th>"+
                   "<td>"+ order.getWarehouseName()+"</td>"+
                   "<td>"+ order.getOblast()+"</td>"+
                   "<td>"+ order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))+"</td>"+
                   "<td>"+ order.getBrand()+"</td></tr>");
           i++;
        }
        ret = ret.concat("</table");
        return ret;
    };
}




