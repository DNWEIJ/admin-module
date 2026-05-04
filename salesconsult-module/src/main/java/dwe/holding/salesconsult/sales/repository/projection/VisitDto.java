package dwe.holding.salesconsult.sales.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class VisitDto {
    Long visitId;
    BigDecimal totalAmountIncTax;
    Long customerId;
    LocalDateTime visitDateTime;
 //   BigDecimal amountCovertSoFar; // this is the amount that has been taken care of by a paymnet
}
