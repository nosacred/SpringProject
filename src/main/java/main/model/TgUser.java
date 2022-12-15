package main.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;

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

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
