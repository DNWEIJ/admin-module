package dwe.holding.generic.admin.model.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public abstract class BaseBO { //} extends ToString {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public boolean isNew() {
        return id == null;
    }
}