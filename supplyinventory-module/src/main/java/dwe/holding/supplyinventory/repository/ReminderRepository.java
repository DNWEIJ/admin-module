package dwe.holding.supplyinventory.repository;

import dwe.holding.customer.client.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByPet_Customer_IdOrderByDueDateDesc(Long customerId);

    @Query("""
                select distinct r.reminderText
                from Reminder r
                where r.memberId = :memberId
                order by r.reminderText
            """)
    List<String> findDistinctReminderTextByMemberIdOrderByReminderText(Long memberId);

    void deleteAllByPet_IdAndReminderTextContainingIgnoreCase(Long petId, String nomenclature);
}