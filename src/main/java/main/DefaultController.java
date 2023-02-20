package main;

import main.model.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class DefaultController {

    @Autowired
    private CustomOrderRepository customOrderRepository;

    @Autowired
    private StocksRepository stocksRepository;

    @Autowired
    private GetOrder getOrder;

    @Autowired
    private GetSale getSale;

    @Autowired
    private GetStock getStock;


    @RequestMapping("/")
    public String index(){
        return ZonedDateTime.now().toString();
    }


    @GetMapping("/minus90")
    public String getYesterDayOrders() throws IOException, InterruptedException {
        getOrder.getNewOrdersMinus90();
        getSale.getNewSalessMinus90();
        return "done";
    }

    @GetMapping("/viewStocks")
    public String viewStocks(){
        ArrayList<Stock> stocks = (ArrayList<Stock>) stocksRepository.findAll();
        stocks.sort(Comparator.comparing(Stock::getBarcode));
        String ret = "<table><tr><th>Артикул вб</th>" +
        "<th>Предмет</th>"+
                "<th>Артикул поставщика </th>"+
                "<th>Размер </th>"+
                "<th>Склад</th>"+
                "<th>Кол-во</th>"+
                "<th>В пути </th></tr>";
        for(Stock stock : stocks){
           ret = ret.concat("<td>"+ stock.getNmId()+"</td>"+
                    "<td>"+ stock.getSubject()+"</td>"+
                    "<td>"+ stock.getSupplierArticle()+"</td>"+
                    "<td>"+ stock.getTechSize()+"</td>"+
                    "<td>"+ stock.getWarehouseName()+"</td>"+
                    "<td>"+ stock.getQuantity()+"</td>"+
                    "<td>"+ (stock.getQuantityFull() - stock.getQuantity())+"</td></tr>");
        }
        ret = ret.concat("</table");
        return ret;
    }

    @GetMapping("/salesOrder")
    public String getSalesOrdersByPeriod(@RequestParam(value = "start",
            defaultValue = "2022-08-25T20:20:20")String start,
                                         @RequestParam(value = "end",defaultValue = "2022-11-30T20:20:20")String end) throws IOException, InterruptedException {

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if(start.isEmpty()){
            start = LocalDateTime.now().minusDays(90).format(formatter);
        };
        if(end.isEmpty()){
            end = LocalDateTime.now().format(formatter);
        }

        LocalDateTime startLd = LocalDateTime.parse(start,formatter);
        LocalDateTime endtLd = LocalDateTime.parse(end,formatter);

        ZonedDateTime zndStart = ZonedDateTime.of(startLd, ZoneId.systemDefault());
        ZonedDateTime zndEnd = ZonedDateTime.of(endtLd, ZoneId.systemDefault());

        while( !zndStart.isAfter(zndEnd)){
            getOrder.getAllOrdersAtDate(zndStart);
            getSale.getAllSalesAtDate(zndStart);
            TimeUnit.MINUTES.sleep(4);
            zndStart = zndStart.plusDays(1);
        }


        return "Процесс выполнен";
    }

    @GetMapping("/stock")
    public String stock() throws IOException {
        getStock.getAllStocks();
        return "Остатки получены";
    }

    @GetMapping("/salesOrderMax")
    public String getSalesOrdersByMaxPeriod() throws IOException, InterruptedException {

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

          String  start = LocalDateTime.of(2022,11,14,20,20,2).format(formatter);


          String  end = LocalDateTime.now().format(formatter);


        LocalDateTime startLd = LocalDateTime.parse(start,formatter);
        LocalDateTime endtLd = LocalDateTime.parse(end,formatter);

        ZonedDateTime zndStart = ZonedDateTime.of(startLd, ZoneId.systemDefault());
        ZonedDateTime zndEnd = ZonedDateTime.of(endtLd, ZoneId.systemDefault());

        while( !zndStart.isAfter(zndEnd)){
            getOrder.getAllOrdersAtDate(zndStart);
            getSale.getAllSalesAtDate(zndStart);
            TimeUnit.MINUTES.sleep(2);
            zndStart = zndStart.plusDays(1);
        }


        return "Процесс выполнен";
    }

    @GetMapping("/today")
    public String getOrderByDay(){

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime zdt2 = zdt.plusDays(1).withHour(0).withMinute(0);
        List<Order> optional = customOrderRepository.findOrderByDateBetweenOrderByDate(zdt,zdt2);
        ArrayList<Order> orders = new ArrayList<>(optional);

        String ret = "<table><tr><th>Номер</th>" +
                "<th>Предмет</th>"+
                "<th>Артикул поставщика </th>"+
                "<th>Размер </th>"+
                "<th>Сумма заказа </th>"+
                "<th>Склад заказа</th>"+
                "<th>Регион заказа </th>"+
                "<th>Дата заказа </th>"+
                "<th>Бренд </th></tr>";

        int i =1;
        for(Order order :orders){

            ret =  ret.concat("<tr>" +"<td>"+ i+"</td>"+
                    "<td>"+ order.getSubject()+"</td>"
                    + "<td>"+ "<a href =\""+order.getWBLink()+"\">"+  order.getSupplierArticle()+"</a></td>"+
                    "<td>"+ order.getTechSize()+"</td>"+
                    "<th>"+ order.getTotalPriceWithDisc()+"</th>"+
                    "<td>"+ order.getWarehouseName()+"</td>"+
                    "<td>"+ order.getOblast()+"</td>"+
                    "<td>"+ order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"))+"</td>"+
                    "<td>"+ order.getBrand()+"</td></tr>");
            i++;
        }
        ret = ret.concat("</table");

        return "Заказов за "+ zdt.format(formatter) + " "+ orders.size() + " штук\n"+ ret;
    }

    @GetMapping("/alltoday")
    public void getAllOrders() throws IOException {
        getOrder.getAllOrdersToday();
    }

}
