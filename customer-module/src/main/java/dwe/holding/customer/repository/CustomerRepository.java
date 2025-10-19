package dwe.holding.customer.repository;

import dwe.holding.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;


public interface CustomerRepository extends JpaRepository<Customer,   Long> {
    Customer findByLastNameStartsWith(String name);

    List<Customer> findByLastNameStartsWithAndMemberId(String lastName, Long currentUserMid);

    List<Customer> findByLastNameContainingAndMemberId(String lastName, Long currentUserMid);

    List<Customer> findByAddressLineContainingAndMemberId(String lastName, Long currentUserMid);

}