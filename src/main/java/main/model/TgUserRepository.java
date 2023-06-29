package main.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TgUserRepository extends CrudRepository<TgUser, Long> {
}
