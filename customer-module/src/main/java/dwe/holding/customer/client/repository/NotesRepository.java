package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotesRepository extends JpaRepository<Notes, Long> {
    List<Notes> findByPet_Customer_IdOrderByNoteDateDesc(Long customerId);

}