package dwe.holding.admin.model.base;

import dwe.holding.admin.tenant.TenantDiscriminator;
import dwe.holding.admin.tenant.TenantEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;



@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, TenantEntityListener.class})
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
public class TenantBaseBO extends BaseBO {
    @Column(nullable = false)
    @TenantDiscriminator
    private   Long memberId;
    private   Long localMemberId;

    public TenantBaseBO() {
    }
}