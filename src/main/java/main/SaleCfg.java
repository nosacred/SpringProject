package main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaleCfg {
    @Bean
    public GetSale getGetSale(){
        return new GetSale();
    }
}
