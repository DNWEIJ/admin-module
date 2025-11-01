package dwe.holding.customer.repository;

import dwe.holding.customer.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    @Query("SELECT r FROM CUSTOMER_REMINDER r  JOIN " +
            " CUSTOMER_PET p on p.id = r.pet.id " +
            "WHERE p.customer.id = ?1")
    List<Reminder> getByCustomerId(Long customerId);

}