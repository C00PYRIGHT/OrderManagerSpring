package rendeleskezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.User;

import java.util.List;
import java.util.Optional;

public interface LampRepository extends JpaRepository<Lamp, Long> {
    Lamp findLampById(Long id);
    Lamp findLampByName(String name);
    @Query("SELECT m.name FROM Lamp m")
    List<String> findAllLampNames();
    @Query("SELECT l FROM Lamp l WHERE l.supply <= :supply")
    List<Lamp> findLampsWithSupplyLessThanOrEqual(@Param("supply") int supply);
}
