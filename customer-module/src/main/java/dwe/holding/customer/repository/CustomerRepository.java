package dwe.holding.customer.repository;

import dwe.holding.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

  

public interface CustomerRepository extends JpaRepository<Customer,   Long> {
    Customer findByLastNameStartsWith(String name);
}