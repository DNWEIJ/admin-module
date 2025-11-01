package dwe.holding.customer.model.lookup;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity(name = "CUSTOMER_LOOKUP_NOTEPURPOSE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupNotePurpose extends MemberBaseBO {
    private String preDefinedPurpose;
}