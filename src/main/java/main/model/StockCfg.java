package main.model;

import main.GetStock;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockCfg {

    public GetStock getGetStock(){
        return  new GetStock();
    }
}
