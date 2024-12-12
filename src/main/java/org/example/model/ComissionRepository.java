package org.example.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComissionRepository extends CrudRepository<Comission,String> {
    Comission findBySubject = new Comission();
}
