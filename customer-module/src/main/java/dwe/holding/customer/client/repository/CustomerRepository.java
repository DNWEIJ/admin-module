package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.client.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByAddress2ContainingAndMemberIdOrderByLastNameAscFirstNameAsc(String prefix, Long currentUserMid);

    List<Customer> findByHomePhoneOrWorkPhoneContainingOrMobilePhoneContainingOrFirstNameContainingAndMemberIdOrderByLastNameAscFirstNameAsc(String homePhone, String workPhone, String mobilePhone,String firstName, Long currentUserMid);

    Optional<Customer> findByIdAndMemberId(Long customerId, Long memberId);

    Customer findByPets_IdAndMemberId(Long petId, Long memberId);

    List<Customer> findByZipCodeAndMemberId(String zipCode, Long MemberId);

    List<Customer> findByZipCodeAndStreetNumberStartingWithAndMemberId(String zipCode, String streetNumber, Long MemberId);

    default List<Customer> findByTelAndMemberIdOrderByLastNameAscFirstNameAsc(String searchCriteria, long memberId) {
        return findByHomePhoneOrWorkPhoneContainingOrMobilePhoneContainingOrFirstNameContainingAndMemberIdOrderByLastNameAscFirstNameAsc(searchCriteria, searchCriteria, searchCriteria,searchCriteria, memberId);
    }

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

    @Query("SELECT c.id FROM Customer c ")
    List<Long> findAllIds();

    @Modifying
    @Query("UPDATE Customer c SET c.balance = :balance WHERE c.id = :id")
    void updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);


    @Query(value = """
            SELECT YEAR(c.addedOn) as year, COUNT(DISTINCT c.id) as customers
            FROM Customer c 
            JOIN c.pets p
            WHERE c.memberId = :memberId
            GROUP BY YEAR(c.addedOn)
            ORDER BY YEAR(c.addedOn) ASC
            """)
    List<Object[]> countCustomersPerYear(@Param("memberId") Long memberId);

    @Query(value = """
            SELECT COUNT(DISTINCT p.customer.id) 
            FROM Appointment a
            JOIN a.visits v 
            JOIN v.pet p
            JOIN p.customer c 
            WHERE a.memberId = :memberId 
            AND c.addedOn > :customerFrom 
            AND c.addedOn < :customerTill
            AND a.visitDateTime > :appointmentFrom
            AND a.visitDateTime <= :appointmentTill
            """)
    Long countNewCustomers(LocalDateTime customerFrom, LocalDateTime customerTill,LocalDateTime appointmentFrom,  LocalDateTime appointmentTill, Long memberId);

    @Query(value = """
            SELECT COUNT(DISTINCT p.customer.id) 
                    FROM Appointment a
            JOIN a.visits v 
            JOIN v.pet p
            JOIN p.customer c 
            JOIN a.lineItems l
            WHERE a.visitDateTime > :from 
            AND a.visitDateTime <= :till 
            AND p.memberId = :memberId
            """)
    Long countActiveCustomers(LocalDateTime from,LocalDateTime till,  Long memberId);

    long countByBalanceIsNull();

    public record CustomerPetDto(Customer customer, Pet pet) {
    }
}