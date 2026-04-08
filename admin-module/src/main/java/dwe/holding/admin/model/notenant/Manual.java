package dwe.holding.admin.model.notenant;


import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "ADMIN_MANUAL", uniqueConstraints = @UniqueConstraint(name = "uk_manual_name", columnNames = {"name"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Manual extends LocalAndMemberBaseBO {
    String name;
    @Lob
    String htmlDescription;
}
