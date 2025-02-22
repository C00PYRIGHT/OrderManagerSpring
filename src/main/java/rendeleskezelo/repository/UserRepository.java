package rendeleskezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rendeleskezelo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserById(Long id);
}
