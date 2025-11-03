package dwe.holding.customer.client.model.lookup;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "CUSTOMER_LOOKUP_ROOM")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupRoom extends TenantBaseBO {
    @NotEmpty
    @Column(nullable = false)
    private String room;
}