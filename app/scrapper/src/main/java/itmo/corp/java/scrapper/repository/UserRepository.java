package itmo.corp.java.scrapper.repository;

import itmo.corp.java.scrapper.model.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
