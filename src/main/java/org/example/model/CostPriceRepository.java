package org.example.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostPriceRepository extends CrudRepository<CostPrice,String> {
    CostPrice getCostPriceByNmId = new CostPrice();
}
