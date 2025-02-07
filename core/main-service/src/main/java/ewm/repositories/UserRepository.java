package ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ewm.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}