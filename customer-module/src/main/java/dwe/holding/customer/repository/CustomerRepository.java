package dwe.holding.customer.repository;

import dwe.holding.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Customer findByLastNameStartsWith(String name);
}