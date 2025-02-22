package rendeleskezelo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rendeleskezelo.model.Customer;
import rendeleskezelo.model.Customer;
import rendeleskezelo.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void AddCustomer(Customer customer) {

        if (customer.getCustomerId() == null || customer.getCustomerId() == 0) {
            int newCustomerId;
            do {
                newCustomerId = 10000 + (int) (Math.random() * 90000);
            } while (customerRepository.existsByCustomerId(newCustomerId));

            customer.setCustomerId(newCustomerId);
        } else {
            customer.setCustomerId(customer.getCustomerId());
        }
        customerRepository.save(customer);
    }

    @Override
    public void UpdateCustomer(Customer customer) {
       Customer customer1 = customerRepository.findCustomerById(customer.getId());
        customer1.setName(customer.getName());
        customer1.setId(customer.getId());
        if (customer.getCustomerId() == null || customer.getCustomerId() == 0) {
            int newCustomerId;
            do {
                newCustomerId = 10000 + (int) (Math.random() * 90000);
            } while (customerRepository.existsByCustomerId(newCustomerId));

            customer1.setCustomerId(newCustomerId);
        } else {
            customer1.setCustomerId(customer.getCustomerId());
        }


            customer1.setEmail(customer.getEmail());
        customerRepository.save(customer1);
    }

    @Override
    public void DeleteCustomer(Customer customer) {
        customerRepository.delete(customer);

    }

    @Override
    public Customer GetCustomerByID(long id) {
        return customerRepository.findCustomerById(id);
    }

    @Override
    public List<Customer> GetAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer GetCustomerByName(String name) {
        return customerRepository.findCustomerByName(name);
    }
}
