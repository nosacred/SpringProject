package main.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TgUserRepository extends CrudRepository<TgUser, Integer> {
}
