package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByCustomer_IdOrderByDeceasedAsc(Long customerId);

    Pet findByIdAndMemberId(Long petId, Long currentUserMid);
}