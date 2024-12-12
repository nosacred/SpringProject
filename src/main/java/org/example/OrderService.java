package org.example;
import org.example.model.CustomOrderRepository;
import org.example.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomOrderRepository customOrderRepository;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//
//    public  HashMap<String,ArrayList<Order>> getOrdersMap(ArrayList<Order> orders){
//        HashSet<String> barcodes = new HashSet<>();
//        HashMap<String,ArrayList<Order>> ordersMap = new HashMap<>();
//        for(Order order : orders){
//            barcodes.add(order.getBarcode()); //Добавляем баркоды в сет  - исключаем повторы
//        }
//        for (String barcode: barcodes) {
//            ArrayList<Order> soloArt = new ArrayList<>();
//            for(Order order : orders){
//                if(order.getBarcode().equals(barcode)){
//                    soloArt.add(order);
//                }
//            }
//            ordersMap.put(barcode, soloArt);
//        }
//        return ordersMap; // возвращаем карту с заказами
//    }
//    public  void ordersPerBarcode(ArrayList<Order> personalOrder){
//        String articleName;
//        personalOrder.sort(Comparator.comparing(Order::getTotalPrice));
//        BigDecimal totalPrice= new BigDecimal("0");
//        for(Order order : personalOrder){
//            totalPrice = totalPrice.add(order.getTotalPriceWithDisc()).setScale(2, RoundingMode.HALF_UP);
//        }
//        articleName = personalOrder.get(0).getSupplierArticle();
//        System.out.println("Всего заказов "+ articleName +  " - " + personalOrder.size()+ " шт на сумму "+
//                totalPrice + " рублей");
//        System.out.println(personalOrder.size());
//    }
//
//    public ArrayList<OrderSum> ordersSumInPeriod (ZonedDateTime start, ZonedDateTime end) throws IOException {
//// Получаем эррей с заказами за период времени из БД
//        start = start.withHour(0).withMinute(0).withSecond(0);
//        end = end.withHour(23).withMinute(59).withSecond(59);
//
//
//        List<Order> optional=  customOrderRepository.findOrderByApiKeyAndDateBetweenOrderByDate( start,end);
//        ArrayList<Order> orderArrayList = (ArrayList< Order >) optional;
//        HashMap<String, ArrayList<Order>> hashMap = getOrdersMap(orderArrayList);
//        ArrayList<OrderSum> orderSums = new ArrayList<>();
//
//        for (Map.Entry<String, ArrayList<Order>> entry : hashMap.entrySet()) {
//            orderSums.add(new OrderSum(entry.getValue()));
//        }
//
//        return  orderSums;
//    }
//
//    public ArrayList<OrderSum> ordersSumInDay(ZonedDateTime start) throws IOException {
//        // ПОлучаем эррей суммы заказов за конкретную дату из БД
//        start = start.withHour(0).withMinute(0).withSecond(0);
//        ZonedDateTime end = start.withSecond(59).withHour(23).withMinute(59);
//
//        List<Order> optional=  customOrderRepository.findOrderByApiKeyAndDateBetweenOrderByDate(start,end);
//        ArrayList<Order> orderArrayList = (ArrayList< Order >) optional;
//        HashMap<String, ArrayList<Order>> hashMap = getOrdersMap(orderArrayList);
//        ArrayList<OrderSum> orderSums = new ArrayList<>();
//
//        for (Map.Entry<String, ArrayList<Order>> entry : hashMap.entrySet()) {
//            orderSums.add(new OrderSum(entry.getValue()));
//        }
//
//        return  orderSums;
//    }



}
