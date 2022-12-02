package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.Sale;
import main.model.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private final String apiKey = "ZTcyNDEyMWMtMDY2OS00M2VjLWIwMTItNjg2ZjdiYjFjODQx";

    public  void getAllSalesAtDate(ZonedDateTime localDate) throws IOException {

        System.out.println("Выкупы за " + localDate);

        String linkOrder = "https://suppliers-stats.wildberries.ru/api/v1/supplier/" +
                "sales?dateFrom=" + localDate.format(formatterDate) + "T21:00:00.000Z&flag=1&key=";
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
        saleRepository.saveAll(s);
        System.out.println(" ВЫкупов за "+ localDate + " получено и добавлено в базу данных - "+ s.size());
    }
}
