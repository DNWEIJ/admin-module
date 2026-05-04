package dwe.holding.salesconsult.sales.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentVisitDTO(
        Long paymentId,
        BigDecimal paymentAmount,
        LocalDate paymentDate,
        Long customerId,
        Long paymentVisitId,
        Long visitId
  //      BigDecimal leftOverAmountOfPayment
) {
}