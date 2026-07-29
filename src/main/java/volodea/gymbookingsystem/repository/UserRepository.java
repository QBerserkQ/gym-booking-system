package volodea.gymbookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import volodea.gymbookingsystem.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
