package org.example;

import org.example.model.CustomStocksRepository;
import org.example.model.Stock;
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
            quantity = quantity +  stock.getQuantity();
        }
        return quantity;
    }

    public int getQuantity(String barcode){
        ArrayList<Stock> stocks=(ArrayList<Stock>) customStocksRepository.findAllByBarcode(barcode);
        return  stocks.stream().map(Stock::getQuantity).reduce(0,Integer::sum);
    }
    public int getQuantityInWayToClientByBarcode(String barcode){
        ArrayList<Stock> stocks=(ArrayList<Stock>) customStocksRepository.findAllByBarcode(barcode);
        return  stocks.stream().map(Stock::getInWayToClient).reduce(0,Integer::sum);
    }

    public int getQuantityInWayFromClientByBarcode(String barcode){
        ArrayList<Stock> stocks=(ArrayList<Stock>) customStocksRepository.findAllByBarcode(barcode);
        return  stocks.stream().map(Stock::getInWayFromClient).reduce(0,Integer::sum);
    }

}
