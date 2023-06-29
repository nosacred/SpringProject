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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GetOrder {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PhotoBaseService photoBaseService;

    DateTimeFormatter formatterDAteTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NJRCI6ImE2YWJkZDU2LWZlMDktNDU5NS05ZWZlLWUxOWRkMDY2MDdlOSJ9.wAtWxrXuugh5OPBV0USe7axFN7f2Vp7HVfLkQhQUw8o";



    public  void getAllOrdersAtDate(String apiKey,ZonedDateTime localDate) throws IOException, InterruptedException {

        System.out.println( "Заказы за "+ localDate.format(formatterDate));


        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ localDate.toLocalDateTime().format(formatterDate)+ "&flag=1&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        while (!(connection.getResponseCode() ==200)){
            TimeUnit.MINUTES.sleep(1);
            url.openConnection();
        }
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();


        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> ord = new ArrayList<>(Arrays.asList(orders));
        for(Order order: ord){
            order.setApiKey(apiKey);
        }
        orderRepository.saveAll(ord);
        ord.forEach(order -> {
            try {
                photoBaseService.setPhotos(order.getNmId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Заказов добавлено/ Обновленно за "+localDate.format(formatterDate) + " " + ord.size() + " штук");
    }


    public  ArrayList<Order> getAllOrdersToday(String apiKey) throws IOException {
        ZonedDateTime localDate = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println("Заказы за " + localDate.format(formatterDate));


        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom=" + localDate.toLocalDateTime().format(formatterDate) + "&flag=1&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();


        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> ord = new ArrayList<>(Arrays.asList(orders));
        for(Order order: ord){
            order.setApiKey(apiKey);
        }
        orderRepository.saveAll(ord);
        ord.forEach(order -> {
            try {
                photoBaseService.setPhotos(order.getNmId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        return ord;
    }

    public boolean checkApi(String api) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println( "Заказы за "+ zndNow.format(formatter));
        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "stocks?dateFrom="+ zndNow.toLocalDateTime().minusMinutes(55).format(formatterDAteTime)+ "&flag=0&key=";
        URL url = new URL(linkOrder + api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",api);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty( "charset", "utf-8");
        System.out.println("Responce code "+ connection.getResponseCode());
        return connection.getResponseCode() == 200;
    }


    public  ArrayList<Order> getNewOrdersNow(String apiKey) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println( "Заказы за "+ zndNow.format(formatter));
        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ zndNow.toLocalDateTime().minusMinutes(55).format(formatterDAteTime)+ "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty( "charset", "utf-8");

        InputStream response = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
//        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> newOrders = new ArrayList<>(Arrays.asList(orders));
        for(Order order: newOrders){
            order.setApiKey(apiKey);
        }
//        orderRepository.saveAll(newOrders);
        newOrders.forEach(order -> {
            try {
                photoBaseService.setPhotos(order.getNmId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Добавлено/ Обновленно "+ newOrders.size() + " штук");
         return newOrders;
    }

    public  ArrayList<Order> getNewOrdersMinus90(String apiKey) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println( "Заказы за "+ zndNow.format(formatter));
        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ zndNow.toLocalDateTime().minusDays(90).withHour(00).withMinute(00).format(formatterDAteTime)+ "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty( "charset", "utf-8");

        InputStream response = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> newOrders = new ArrayList<>(Arrays.asList(orders));
        for(Order order: newOrders){
            order.setApiKey(apiKey);
        }
        orderRepository.saveAll(newOrders);
        newOrders.forEach(order -> {
            try {
                photoBaseService.setPhotos(order.getNmId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Добавлено/ Обновленно "+ newOrders.size() + " штук");
        return newOrders;
    }

    public  ArrayList<Order> getNewOrdersMinusYear(String apiKey) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println( "Заказы за "+ zndNow.format(formatter));
        String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ zndNow.toLocalDateTime().minusYears(1).withHour(00).withMinute(00).format(formatterDAteTime)+ "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty( "charset", "utf-8");

        InputStream response = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Order[] orders = gson.fromJson(result, Order[].class);
        ArrayList<Order> newOrders = new ArrayList<>(Arrays.asList(orders));
        for(Order order: newOrders){
            order.setApiKey(apiKey);
        }
        orderRepository.saveAll(newOrders);
        newOrders.forEach(order -> {
            try {
                photoBaseService.setPhotos(order.getNmId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Добавлено/ Обновленно "+ newOrders.size() + " штук");
        return newOrders;
    }
}
