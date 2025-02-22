package rendeleskezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.Status;
import rendeleskezelo.model.User;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    @Query("SELECT m.name FROM Status m")
    List<String> findAllStatusNames();

}
