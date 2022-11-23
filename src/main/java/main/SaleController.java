package main;

import main.model.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaleController {

    @Autowired
    private SaleRepository saleRepository ;
}
