package dwe.holding.cartracker.migration.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Deprecated
public class TenantBaseBO extends BaseBO {
    @Column(nullable = false)
    private   UUID  memberId;
    private UUID localMemberId;

    public TenantBaseBO() {
    }
}