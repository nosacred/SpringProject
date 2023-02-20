package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.Sale;
import main.model.Stock;
import main.model.StocksRepository;
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
public class GetStock {
    @Autowired
    StocksRepository stocksRepository;

    public  void getAllStocks() throws IOException {


        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime localDate = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(2);
        System.out.println("Остатки за " + localDate.format(formatterDate));
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NJRCI6ImE2YWJkZDU2LWZlMDktNDU5NS05ZWZlLWUxOWRkMDY2MDdlOSJ9.wAtWxrXuugh5OPBV0USe7axFN7f2Vp7HVfLkQhQUw8o";
        String linkOrder = " https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "stocks?dateFrom=" + localDate.format(formatterDate) + "T21:00:00.000Z&key=";
        URL url = new URL(linkOrder + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",apiKey);
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));

        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();
        Stock[] stocks = gson.fromJson(result, Stock[].class);
        System.out.println(result);
        ArrayList<Stock> s = new ArrayList<>(Arrays.asList(stocks));
        stocksRepository.saveAll(s);
        if(s.size()> 0) {
            System.out.println("Данные остатков получены "+ s.size());
        } else System.out.println("Данные за "+ localDate.format(formatterDate) + " не получены!");

    }
}
