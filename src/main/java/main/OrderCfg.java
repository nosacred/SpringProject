package main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderCfg {

    @Bean
    public OrderService getOrderService(){
        return new OrderService();
    }
    @Bean
    public GetOrder getGetOrder(){
        return  new GetOrder();
    }



}
