package dwe.holding.salesconsult.consult.repository;

import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.salesconsult.sales.repository.projection.VisitDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<VisitProjection> findByMemberIdAndPet_IdInOrderByAppointment_VisitDateTimeDesc(Long memberId, List<Long> petId);

    Optional<Visit> findByMemberIdAndId(Long memberId, Long visitId);

    List<Visit> findByMemberIdAndPet_Id(Long memberId, Long petId);

    List<Visit> findByMemberIdAndIdIn(Long memberId, List<Long> ids);

    long countByTotalAmountIncTaxEquals(BigDecimal value);

    // this is a native mariaDB query. Since we else have to query three times (JPQL) the lineitems, sum every amount separately
    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE consult_visit v
            JOIN (
                SELECT l.appointment_id, l.pet_id,
                       COALESCE(SUM(l.total_inc_tax), 0)                        AS total_inc_tax,
                       COALESCE(SUM(l.tax_portion_of_processing_fee_service), 0) AS total_service_tax,
                       COALESCE(SUM(l.tax_portion_of_product), 0)               AS total_product_tax
                FROM sales_line_item l
                GROUP BY l.appointment_id, l.pet_id
            ) l ON l.appointment_id = v.appointment_id AND l.pet_id = v.pet_id
            SET v.total_amount_inc_tax  = l.total_inc_tax,
                v.total_service_tax     = l.total_service_tax,
                v.total_product_tax     = l.total_product_tax
            """)
    void updateAllTotalAmountsMaintenanceMethod();

    @Modifying
    @Query(nativeQuery = true, value = """
                    UPDATE consult_visit SET total_amount_inc_tax = 0, total_service_tax = 0, total_product_tax = 0;
            """)
    void zeroAmountOnVisitMaintenanceMethod();

    Optional<Visit> findByAppointment_IdAndPet_Id(@NotNull Long appointmentId, @NotNull Long petId);

    @Query("""
    select new dwe.holding.salesconsult.sales.repository.projection.VisitDto(
                   v.id,
                   v.totalAmountIncTax,
                   c.id,
                   a.visitDateTime
               )
    from Visit v
    join v.pet p
    join p.customer c
    join v.appointment a
    where c.id in :customerIds
    order by c.id asc, a.visitDateTime asc
""")
    List<VisitDto> findByPet_CustomerIdInOrderByPet_CustomerIdAscAppointment_visitDateTimeAsc(List<Long> customerIds);




    @Query("""
            select new dwe.holding.salesconsult.sales.repository.projection.VisitDto(
                   v.id,
                   v.totalAmountIncTax,
                   c.id,
                   a.visitDateTime)
            from Visit v
            join v.pet p
            join p.customer c
            join v.appointment a
            WHERE v.paymentVisits IS EMPTY and v.totalAmountIncTax <> 0.0
            """)
    List<VisitDto> findMigrationNotConnectedAndNotZeroAmount();

    @Query("""
            select new dwe.holding.salesconsult.sales.repository.projection.VisitDto(
                   v.id,
                   v.totalAmountIncTax,
                   c.id,
                   a.visitDateTime)
            from Visit v
            join v.pet p
            join p.customer c
            join v.appointment a
            WHERE v.paymentVisits IS EMPTY
            """)
    List<VisitDto> findMigrationNotConnected();


    List<Visit> findByPet_Customer_Id(Long customerId);
}