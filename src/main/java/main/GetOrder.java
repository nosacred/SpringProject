package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.Order;
import main.model.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class GetOrder {
    @Autowired
    OrderRepository orderRepository;
    private final ArrayList<Order> ordersArr = new ArrayList<>();
    DateTimeFormatter formatterDAteTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public  void getAllOrdersAtDate(ZonedDateTime localDate) throws IOException {

        System.out.println( "Заказы за "+ localDate.format(formatterDate));

        String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";
        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ localDate.toLocalDateTime().format(formatterDate)+ "&flag=1&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();


        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> ord = new ArrayList<>(Arrays.asList(orders));
        ordersArr.addAll(ord);
        orderRepository.saveAll(ordersArr);
        System.out.println("Заказов добавлено/ Обновленно за "+localDate.format(formatterDate) + " " + ordersArr.size() + " штук");
        ordersArr.clear();
    }


    public  ArrayList<Order> getNewOrdersNow() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println( "Заказы за "+ zndNow.format(formatter));

        String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";
        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ zndNow.toLocalDateTime().format(formatterDAteTime)+ "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> newOrders = new ArrayList<>(Arrays.asList(orders));
        orderRepository.saveAll(newOrders);
        System.out.println("Добавлено/ Обновленно "+ newOrders.size() + " штук");
         return newOrders;
    }
}
