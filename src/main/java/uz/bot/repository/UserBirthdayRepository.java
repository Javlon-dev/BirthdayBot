package uz.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.bot.variable.entity.UserBirthday;

import javax.transaction.Transactional;
import java.util.List;

public interface UserBirthdayRepository extends JpaRepository<UserBirthday, Long> {

    @Query("select u from UserBirthday u where u.hasDeleted = false")
    List<UserBirthday> findWhereDeletedFalse();

    @Modifying
    @Transactional
    @Query("update UserBirthday u set u.hasDeleted = true, u.telegramUserId = :telegramUserId where u.id = :id and u.hasDeleted = false")
    int updateHasDeleted(Long telegramUserId, Long id);

    @Query(value = "select u.* from user_birthday u " +
            "where u.has_deleted = false " +
            "and (upper(u.name) like upper(concat('%',:data,'%')) " +
            "or upper(u.surname) like upper(concat('%',:data,'%')) " +
            "or cast(u.birth_date as text) like concat('%',:data,'%'))", nativeQuery = true)
    List<UserBirthday> findByData(String data);

}