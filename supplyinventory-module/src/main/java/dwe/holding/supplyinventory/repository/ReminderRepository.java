package dwe.holding.supplyinventory.repository;

import dwe.holding.customer.client.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByPet_Customer_IdOrderByDueDateDesc(Long customerId);

    List<String> findDistinctReminderTextByMemberIdOrderByReminderText(Long memberId);

    void deleteAllByPet_IdAndReminderTextContainingIgnoreCase(Long petId, String nomenclature);

    List<Reminder> findTop5ByPet_idInAndMemberIdAndDueDateGreaterThanOrderByDueDate(List<Long> petIds, Long memberId, LocalDate now);
}