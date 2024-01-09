package itmo.corp.java.scrapper.repository;

import itmo.corp.java.scrapper.model.commonDto.Status;
import itmo.corp.java.scrapper.model.jpa.DayEntity;
import itmo.corp.java.scrapper.model.jpa.OrderEntity;
import itmo.corp.java.scrapper.model.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByDayAndUserAndStatusNot(DayEntity day, UserEntity user, Status status);

    List<OrderEntity> findAllByUser(UserEntity user);

}
