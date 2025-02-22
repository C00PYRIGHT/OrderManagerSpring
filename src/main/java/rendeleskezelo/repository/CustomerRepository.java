package rendeleskezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rendeleskezelo.model.Customer;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.User;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findCustomerById(java.lang.Long id);
    Customer findCustomerByName(String name);
    boolean existsByCustomerId(Integer customerId);
    @Query("SELECT m.name FROM Customer m")
    List<String> findAllCustomerNames();





}
