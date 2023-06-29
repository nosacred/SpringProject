package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.CustomStocksRepository;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetStock {
    @Autowired
    StocksRepository stocksRepository;

    public  void getAllStocks(String api) throws IOException {

        System.out.println("Кол-во обьектов склада по АПИ "+ stocksRepository.findAllByApiKey(api).size());
        List<Stock> dbStock = stocksRepository.findAllByApiKey(api);

        DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime localDate = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(3);
        System.out.println("Остатки за " + localDate.format(formatterDate));
        String linkOrder = " https://statistics-api.wildberries.ru/api/v1/supplier/" +
                "stocks?dateFrom=" + localDate.format(formatterDate) + "T11:00:00.000Z&key=";
        URL url = new URL(linkOrder + api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",api);
        InputStream response = connection.getInputStream();
        String result = new BufferedReader(new InputStreamReader(response)).lines()
                .parallel().collect(Collectors.joining("\n"));

        Gson gson  = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, GsonHelper.ZDT_DESERIALIZER)
                .create();
        Stock[] stocks = gson.fromJson(result, Stock[].class);
//        System.out.println(result);
        ArrayList<Stock> newStocks = new ArrayList<>(Arrays.asList(stocks));
        for(Stock st : dbStock){
                stocksRepository.delete(st);
                System.out.println(" Удален");
        }


        for(Stock stock : newStocks){
            stock.setApiKey(api);
            System.out.println(stock.getSupplierArticle()+ " " + stock.getWarehouseName() + " " + String.format(stock.getLastChangeDate(), ".{19}") + " " + stock.getQuantity()+ " шт");
        }

        stocksRepository.saveAll(newStocks);
        if(newStocks.size()> 0) {
            System.out.println("Данные остатков получены "+ newStocks.size());
        } else System.out.println("Данные за "+ localDate.format(formatterDate) + " не получены!");

    }
}
