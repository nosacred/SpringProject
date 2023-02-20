package main;

import main.model.CustomStocksRepository;
import main.model.Stock;
import main.model.StocksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StockService {
    @Autowired
    CustomStocksRepository customStocksRepository;

    public int getQuantityInWay(ArrayList<Stock> stocks){
        int quantity=0;
        for(Stock stock : stocks){
            quantity = quantity+  (stock.getQuantityFull()-stock.getQuantity());
        }
        return quantity;
    }

    public int getQuantity(ArrayList<Stock> stocks){
        int quantity=0;
        for(Stock stock : stocks){
            quantity = quantity+  stock.getQuantity();
        }
        return quantity;
    }

}
