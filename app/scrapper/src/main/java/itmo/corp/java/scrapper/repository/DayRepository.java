package itmo.corp.java.scrapper.repository;

import itmo.corp.java.scrapper.model.jpa.DayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DayRepository extends JpaRepository<DayEntity, Long> {

    Optional<DayEntity> findByDate(LocalDate date);

}
