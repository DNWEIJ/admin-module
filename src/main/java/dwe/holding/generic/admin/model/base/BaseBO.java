package dwe.holding.generic.admin.model.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public abstract class BaseBO {

    @Id
    @Column(columnDefinition = "UUID")
    // @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Version
    @Column(nullable = false)
    private Long version;
    @CreatedBy
    private String addedBy;
    @CreatedDate
    private Instant addedOn;
    @LastModifiedBy
    private String lastEditedBy;
    @LastModifiedDate
    private Instant lastEditedOn;

    public BaseBO() {
    }

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public boolean isNew() {
        return id == null;
    }
}