package uz.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.bot.variable.entity.TelegramUser;

import javax.transaction.Transactional;
import java.util.List;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    @Query("select t from TelegramUser t where t.hasRead = true ")
    List<TelegramUser> getReceiversList();

    @Modifying
    @Transactional
    @Query("update TelegramUser t set t.hasRead = :data where t.id = :id")
    void updateHasRead(Long id, boolean data);

    @Modifying
    @Transactional
    @Query("update TelegramUser t set t.hasWrite = :data where t.id = :id")
    void updateHasWrite(Long id, boolean data);

}