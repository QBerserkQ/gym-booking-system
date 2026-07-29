package volodea.gymbookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import volodea.gymbookingsystem.entity.GymClass;

public interface GymRepository extends JpaRepository<GymClass, Long> {
}
