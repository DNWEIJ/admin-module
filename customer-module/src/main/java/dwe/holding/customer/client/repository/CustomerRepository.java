package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CustomerRepository extends JpaRepository<Customer,   Long> {
    @Query("""
                SELECT c FROM Customer c
                WHERE LOWER(c.lastName) LIKE LOWER(CONCAT(:prefix, '%'))
                  AND c.memberId = :currentUserMid
                ORDER BY 
                  CASE WHEN LOWER(c.lastName) LIKE LOWER(CONCAT(:prefix, '%')) THEN 0 ELSE 1 END,
                  c.lastName ASC,
                  c.firstName ASC
            """)
    List<Customer> getCustomerStartLastName(String prefix, Long currentUserMid);

    @Query("""
                SELECT c FROM Customer c
                WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%',CONCAT(:prefix, '%')))
                  AND c.memberId = :currentUserMid
                ORDER BY 
                  CASE WHEN LOWER(c.lastName) LIKE LOWER(CONCAT('%',CONCAT(:prefix, '%'))) THEN 0 ELSE 1 END,
                  c.lastName ASC,
                  c.firstName ASC
            """)
    List<Customer> getCustomerSomewhereLastName(String prefix, Long currentUserMid);

    List<Customer> findByAddressLineContainingAndMemberIdOrderByLastNameAscFirstNameAsc(String prefix, Long currentUserMid);

}