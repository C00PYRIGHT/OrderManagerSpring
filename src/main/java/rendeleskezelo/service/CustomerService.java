package rendeleskezelo.service;

import rendeleskezelo.model.Customer;

import java.util.List;

public interface CustomerService {
    void AddCustomer(Customer customer);
    void UpdateCustomer(Customer customer);
    void DeleteCustomer(Customer customer);
    Customer GetCustomerByID(long id);
    List<Customer> GetAllCustomers();
    Customer GetCustomerByName(String name);



}
