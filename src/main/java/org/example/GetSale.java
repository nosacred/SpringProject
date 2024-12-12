package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Sale;
import org.example.model.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GetSale {
    @Autowired
    SaleRepository saleRepository;

    DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NJRCI6ImE2YWJkZDU2LWZlMDktNDU5NS05ZWZlLWUxOWRkMDY2MDdlOSJ9.wAtWxrXuugh5OPBV0USe7axFN7f2Vp7HVfLkQhQUw8o";

    public void getAllSalesAtDate(String apiKey, ZonedDateTime localDate) throws IOException {
        try {
            System.out.println("Выкупы за " + localDate);

            String linkOrder = "https://statistics-api.wildberries.ru/api/v1/supplier/" +
                    "sales?dateFrom=" + localDate.format(formatterDate) + "T21:00:00.000Z&flag=1&key=";
            URL url = new URL(linkOrder + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", apiKey);
            InputStream response = connection.getInputStream();
            System.out.println("Responce code = " + connection.getResponseCode());

            String result = new BufferedReader(new InputStreamReader(response)).lines()
                    .parallel().collect(Collectors.joining("\n"));

//            System.out.println(result);
            Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                    .create();
            Sale[] sales = gson.fromJson(result, Sale[].class);
            ArrayList<Sale> s = new ArrayList<>(Arrays.asList(sales));
            for (Sale sale : s) {
                sale.setApiKey(apiKey);
            }
            saleRepository.saveAll(s);
            System.out.println(" ВЫкупов за " + localDate + " получено и добавлено в базу данных - " + s.size());
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public ArrayList<Sale> getNewSalessMinus90(String apiKey) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(90);
        System.out.println("Заказы за " + zndNow.format(formatter));
        String linkOrder = " https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "sales?dateFrom=" + zndNow.toLocalDateTime().format(formatter) + "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");

        InputStream response = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Sale[] sales = gson.fromJson(result, Sale[].class);
        ArrayList<Sale> newSales = new ArrayList<>(Arrays.asList(sales));
        for (Sale sale : newSales) {
            sale.setApiKey(apiKey);
        }
        saleRepository.saveAll(newSales);
        System.out.println("Добавлено/ Обновленно Выкупов " + newSales.size() + " штук");
        return newSales;
    }

    public ArrayList<Sale> getNewSalessMinusYear(String apiKey) throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime zndNow = ZonedDateTime.now(ZoneId.systemDefault()).minusYears(1);
        System.out.println("Заказы за " + zndNow.format(formatter));
        String linkOrder = " https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "sales?dateFrom=" + zndNow.toLocalDateTime().format(formatter) + "&flag=0&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");

        InputStream response = connection.getInputStream();

        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));
        System.out.println(result);
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();

        Sale[] sales = gson.fromJson(result, Sale[].class);
        ArrayList<Sale> newSales = new ArrayList<>(Arrays.asList(sales));
        for (Sale sale : newSales) {
            sale.setApiKey(apiKey);
        }
        saleRepository.saveAll(newSales);
        System.out.println("Добавлено/ Обновленно Выкупов " + newSales.size() + " штук");
        return newSales;
    }
}
