package dwe.holding.salesconsult.sales.repository;


import dwe.holding.salesconsult.sales.model.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface LineItemRepository extends JpaRepository<LineItem, Long> {

    @Query("SELECT sum(l.totalIncTax) from LineItem as l WHERE l.pet.customer.id = :customerId and l.memberId = :memberId")
    BigDecimal getSumAmountOfLineItem(@Param("customerId") Long customerId, @Param("memberId") Long memberId);

    @Query("SELECT sum(l.totalIncTax) from LineItem as l WHERE l.pet.customer.id = :customerId and l.memberId = :memberId and l.appointment.visitDateTime <= :limitDate")
    BigDecimal getSumAmountOfLineItem(@Param("customerId") Long customerId, @Param("limitDate") LocalDateTime limitDate, @Param("memberId") Long memberId);

    List<LineItem> findByPet_IdAndAppointment_IdAndMemberId(Long petId, Long appointmentId, Long memberId);
}