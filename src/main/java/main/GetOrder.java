package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.Order;
import main.model.OrderRepository;

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

public class GetOrder {
    public static ArrayList<Order> ordersArr = new ArrayList<>();

    public static void getOrdersAtDate(ZonedDateTime localDate) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        System.out.println( "Заказы за "+ localDate.format(formatter));

        String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";
        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ localDate.toLocalDateTime().format(formatter)+ "&flag=1&key=";
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
    }

    public static ArrayList<Order> getNewOrdersNow() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1);
        System.out.println( "Заказы за "+ zndNow.format(formatter));

        String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";
        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "orders?dateFrom="+ zndNow.toLocalDateTime().format(formatter)+ "&flag=0&key=";
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
         return new ArrayList<>(Arrays.asList(orders));
    }
}
