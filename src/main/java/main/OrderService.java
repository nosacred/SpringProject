package main;
import main.model.Order;
import main.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class OrderService {
    @Autowired
    private static OrderRepository orderRepository;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        orderRepository.saveAll(orders);

        return orders;

    }

    public static HashMap<String,ArrayList<Order>> getOrdersMap(ArrayList<Order> orders){
        HashSet<String> barcodes = new HashSet<>();
        HashMap<String,ArrayList<Order>> ordersMap = new HashMap<>();
        for(Order order : orders){
            barcodes.add(order.getBarcode()); //Добавляем баркоды в сет  - исключаем повторы
        }
        for (String barcode: barcodes) {
            ArrayList<Order> soloArt = new ArrayList<>();
            for(Order order : orders){
                if(order.getBarcode().equals(barcode)){
                    soloArt.add(order);
                }
            }
            ordersMap.put(barcode, soloArt);
        }
        return ordersMap; // возвращаем карту с заказами
    }
    public static void ordersPerBarcode(ArrayList<Order> personalOrder){
        String articleName;
        personalOrder.sort(Comparator.comparing(Order::getTotalPrice));
        BigDecimal totalPrice= new BigDecimal("0");
        for(Order order : personalOrder){
            totalPrice = totalPrice.add(order.getTotalPriceWithDisc()).setScale(2, RoundingMode.HALF_UP);
        }
        articleName = personalOrder.get(0).getSupplierArticle();
        System.out.println("Всего заказов "+ articleName +  " - " + personalOrder.size()+ " шт на сумму "+
                totalPrice + " рублей");
        System.out.println(personalOrder.size());
    }


}
