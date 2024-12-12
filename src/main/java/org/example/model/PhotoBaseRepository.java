package org.example.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoBaseRepository extends CrudRepository<PhotoBase, String> {
}
