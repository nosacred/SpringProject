package main;

import main.model.Order;
import main.model.OrderRepository;
import main.model.OrderSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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




//    @GetMapping("/get")
//    public  String setOrders(@RequestParam (value = "date",required = false, defaultValue = "2022-12-02T20:20:20") String date) throws IOException {
//        DateTimeFormatter formatter
//                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        LocalDateTime ld = LocalDateTime.parse(date,formatter);
//        ZonedDateTime znd = ZonedDateTime.of(ld,ZoneId.systemDefault());
//        System.out.println(znd);
//
//        try {
//
//            getOrder.getAllOrdersAtDate(znd);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return "Данные по заказам за " + ld.format(formatter)+ " получены";
//    }
//
//
//    @RequestMapping("/showAll")
//    public String showOrders(){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
//        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Order.by("date")));
//        String ret = "<table><tr><th>Номер</th>" +
//                "<th>Предмет</th>"+
//                "<th>Артикул поставщика </th>"+
//                "<th>Размер </th>"+
//                "<th>Сумма заказа </th>"+
//                "<th>Склад заказа</th>"+
//                "<th>Регион заказа </th>"+
//                "<th>Дата заказа </th>"+
//                "<th>Бренд </th></tr>";
//
//        int i =1;
//        for(Order order :orders){
//
//            ret =  ret.concat("<tr>" +"<td>"+ i+"</td>"+
//                    "<td>"+ order.getSubject()+"</td>"
//                    + "<td>"+ "<a href =\""+order.getWBLink()+"\">"+  order.getSupplierArticle()+"</a></td>"+
//                    "<td>"+ order.getTechSize()+"</td>"+
//                    "<th>"+ order.getTotalPriceWithDisc()+"</th>"+
//                    "<td>"+ order.getWarehouseName()+"</td>"+
//                    "<td>"+ order.getOblast()+"</td>"+
//                    "<td>"+ order.getDate().format(formatter))+"</td>"+
//                    "<td>"+ order.getBrand()+"</td></tr>";
//            i++;
//        }
//        ret = ret.concat("</table");
//        return ret;
//    }
//
//
//    @RequestMapping("/new")
//    public String newOrdersNow() throws IOException, InterruptedException {
//        ArrayList<Order> newOrders = getOrder.getNewOrdersNow();
//        String ret = "<table><tr><th>Предмет</th>" +
//                "<th>Артикул поставщика </th>"+
//                "<th>Размер </th>"+
//                "<th>Сумма заказа </th>"+
//                "<th>Склад заказа</th>"+
//                "<th>Регион заказа </th>"+
//                "<th>Дата заказа </th>"+
//                "<th>Бренд </th></tr>";
//
//        int i =1;
//        for(Order order :newOrders){
//
//           ret =  ret.concat("<tr>" +"<td>"+ i+"</td>"+
//                   "<td>"+ order.getSubject()+"</td>"
//        + "<td>"+ "<a href =\""+order.getWBLink()+"\">"+  order.getSupplierArticle()+"</a></td>"+
//                   "<td>"+ order.getTechSize()+"</td>"+
//                   "<th>"+ order.getTotalPriceWithDisc()+"</th>"+
//                   "<td>"+ order.getWarehouseName()+"</td>"+
//                   "<td>"+ order.getOblast()+"</td>"+
//                   "<td>"+ order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"))+"</td>"+
//                   "<td>"+ order.getBrand()+"</td></tr>");
//           i++;
//        }
//        ret = ret.concat("</table");
//        return ret;
//    };
//
//    @RequestMapping("/sum")
//    public String orderSums() throws IOException {
//        ZonedDateTime zndyesterDay = ZonedDateTime.now().minusDays(1).withZoneSameLocal(ZoneId.systemDefault());
//        ArrayList<OrderSum> newOrders = orderService.ordersSumInDay(zndyesterDay);
//        String ret = "<table><tr>" +
//                "<th>Номер пп</th>"+
//                "<th>Предмет</th>" +
//                "<th>Артикул поставщика </th>"+
//                "<th>Размер </th>"+
//                "<th>Кол-во </th>"+
//                "<th>Сумма заказов</th>"+
//                "<th>Кол-во Отказа </th>"+
//                "<th>Бренд </th></tr>";
//        Collections.sort(newOrders);
//        Collections.reverse(newOrders);
//
//        int i =1;
//        for(OrderSum order :newOrders){
//
//            ret =  ret.concat("<tr>" +"<td>"+ i+"</td>"+
//                    "<td>"+ order.getSubject()+"</td>"
//                    + "<td>"+ "<a href =\""+ order.getLink()+"\">"+  order.getName()+"</a></td>"+
//                    "<th>"+ order.getTechSize()+"</th>"+
//                    "<td>"+ order.getOrderQuantity()+"</td>"+
//                    "<td>"+ order.getTotalOrdersPriceSum()+"</td>"+
//                    "<td>"+ order.getCancelCount()+"</td>"+
//                    "<td>"+ order.getBrand()+"</td></tr>");
//            i++;
//        }
//        ret = ret.concat("</table");
//        return ret;
//    }
}




