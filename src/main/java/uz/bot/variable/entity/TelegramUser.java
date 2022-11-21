package uz.bot.variable.entity;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;

@Entity
@Table(name = "telegram_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TelegramUser {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_name", columnDefinition = "text")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "text")
    private String lastName;

    @Column(name = "has_write", columnDefinition = "boolean default false", nullable = false)
    private boolean hasWrite;

    @Column(name = "has_read", columnDefinition = "boolean default false", nullable = false)
    private boolean hasRead;

    public TelegramUser(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

}
