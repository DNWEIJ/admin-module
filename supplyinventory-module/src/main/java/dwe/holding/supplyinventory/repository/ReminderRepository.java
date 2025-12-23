package dwe.holding.supplyinventory.repository;

import dwe.holding.customer.client.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByPet_Customer_IdOrderByDueDateDesc(Long customerId);
}