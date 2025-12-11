package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
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

    List<Customer> findByHomePhoneOrWorkPhoneContainingOrMobilePhoneContainingAndMemberIdOrderByLastNameAscFirstNameAsc(String homePhone, String workPhone, String mobilePhone, Long currentUserMid);

    Optional<Customer> findByIdAndMemberId(Long customerId, Long memberId);

    Customer findByPets_IdAndMemberId(Long patientId, long l);


    default List<Customer> findByTelAndMemberIdOrderByLastNameAscFirstNameAsc(String searchCriteria, long memberId) {
        return findByHomePhoneOrWorkPhoneContainingOrMobilePhoneContainingAndMemberIdOrderByLastNameAscFirstNameAsc(searchCriteria, searchCriteria, searchCriteria, memberId);

    }

    @Query("""
                SELECT c, p FROM Customer c JOIN c.pets p
                WHERE (
                     p.name LIKE CONCAT('%', :searchCriteria, '%')
                  OR p.chipTattooId LIKE CONCAT('%', :searchCriteria, '%')
                  OR p.passportNumber LIKE CONCAT('%', :searchCriteria, '%')
               )
               AND p.memberId = :memberId
               ORDER BY   c.lastName ASC, c.firstName ASC
            """)
    List<CustomerPetDto> findByPet(String searchCriteria, long memberId);
    public record CustomerPetDto(Customer customer, Pet pet) {}
}