package org.example.model;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CustomTgUserRepository extends CrudRepository<TgUser, Long> {
    ArrayList<TgUser> getByApi(String api);
}
