package dwe.holding.customer.client.repository;

import dwe.holding.customer.client.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByPet_Customer_IdOrderByNoteDateDesc(Long customerId);

}