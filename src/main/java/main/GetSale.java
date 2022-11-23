package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.Order;
import main.model.Sale;
import main.model.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GetSale {
    public static ArrayList<Sale> salessArr = new ArrayList<>();


    public static void getSalesAtDate(ZonedDateTime localDate) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        System.out.println("Выкупы за " + localDate);
        String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";
        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "sales?dateFrom=" + localDate.format(formatter) + "T21:00:00.000Z&flag=1&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));

        System.out.println(result);
        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();
        Sale[] sales = gson.fromJson(result, Sale[].class);
        ArrayList<Sale> s = new ArrayList<>(Arrays.asList(sales));
        salessArr.addAll(s);
    }
}
