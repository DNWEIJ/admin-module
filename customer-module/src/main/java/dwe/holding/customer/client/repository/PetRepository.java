package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByCustomer_IdOrderByDeceasedAsc(Long customerId);

    Pet findByIdAndMemberId(Long petId, Long currentUserMid);


    @Query(value = """
            SELECT p.species, COUNT(DISTINCT p.id) 
            
            FROM Appointment a
            JOIN a.visits v
            Join v.pet p
            JOIN a.lineItems l
            WHERE a.visitDateTime > :from 
            AND a.visitDateTime <= :till
            AND p.deceased = 'N' 
            AND p.memberId = :memberId
            GROUP BY p.species
            """)
    List<Object[]> countPatientsBySpeciesForActiveCustomers(LocalDateTime from, LocalDateTime till, Long memberId);

}
