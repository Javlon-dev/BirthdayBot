package uz.bot.variable.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_birthday")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserBirthday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "text")
    private String name;

    @Column(name = "surname", columnDefinition = "text")
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "telegram_user")
    private Long telegramUserId;

    @ManyToOne
    @JoinColumn(name = "telegram_user", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    private TelegramUser telegramUser;

    @Column(name = "has_deleted", columnDefinition = "boolean default false", nullable = false)
    private boolean hasDeleted;

    public UserBirthday(Long telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

}
