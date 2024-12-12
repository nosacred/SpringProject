package org.example.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tg_users")
public class TgUser {
    public long getChatId() {
        return chatId;
    }


    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Id
    private long chatId;

    private String firstName;
    @Column(columnDefinition="text")
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
