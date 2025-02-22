package rendeleskezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rendeleskezelo.model.LitophaneOrder;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<LitophaneOrder, Long> {

    // Rendelés keresése ID alapján
    LitophaneOrder findOrderById(Long id);

    // Rendelés keresése név alapján
    LitophaneOrder findOrderByName(String name);

    // Összes rendelés neve (egyedi lekérdezés)
    @Query("SELECT m.name FROM LitophaneOrder m")
    List<String> findAllOrderNames();

    // Rendelések keresése ügyfél ID alapján
    List<LitophaneOrder> findByCustomerId(Long customerId);

    // Rendelések keresése státusz ID alapján
    List<LitophaneOrder> findByStatusId(Long statusId);



    @Query("SELECT o FROM LitophaneOrder o WHERE o.deadline BETWEEN :today AND :nextWeek")
    List<LitophaneOrder> findOrdersWithDeadlineBetween(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);




    // Rendelések lekérdezése anyagköltség alapján (például, ha 10 000 Ft alatt)
    List<LitophaneOrder> findByMaterialCostLessThan(Integer amount);

    // Rendelések bevétel alapján (például, ha nagyobb mint 50 000 Ft)
    List<LitophaneOrder> findByRevenueGreaterThan(Integer amount);

    // Adott ügyfélhez és státuszhoz tartozó rendelés
    List<LitophaneOrder> findByCustomerIdAndStatusId(Long customerId, Long statusId);



}
