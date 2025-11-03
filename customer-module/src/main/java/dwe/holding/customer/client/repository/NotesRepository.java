package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface NotesRepository extends JpaRepository<Notes, Long> {

    @Query("SELECT n FROM CUSTOMER_NOTES n  JOIN " +
            " CUSTOMER_PET p on p.id = n.pet.id " +
            "WHERE p.customer.id = ?1 ORDER BY n.noteDate desc")
    List<Notes> getByCustomerId(Long customerId);

}