package main;

import main.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RestController
public class DefaultController {

    @Autowired
    private GetOrder getOrder;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GetSale getSale;

    @RequestMapping("/")
    public String index(){
        return ZonedDateTime.now().toString();
    }

    @GetMapping("/salesOrder")
    public String getSalesOrdersByPeriod(@RequestParam(value = "start",
            defaultValue = "2022-08-25T20:20:20")String start,
                                         @RequestParam(value = "end",defaultValue = "2022-11-25T20:20:20")String end) throws IOException, InterruptedException {

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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


}
