package volodea.gymbookingsystem.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import volodea.gymbookingsystem.entity.GymClass;

import java.util.Optional;

public interface GymRepository extends JpaRepository<GymClass, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GymClass g WHERE g.id = :id")
    Optional<GymClass> findByIdForUpdate(@Param("id")long id);
}
