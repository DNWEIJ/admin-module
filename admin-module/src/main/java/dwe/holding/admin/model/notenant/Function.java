package dwe.holding.admin.model.notenant;

import dwe.holding.admin.model.base.BaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "ADMIN_FUNCTION", uniqueConstraints = @UniqueConstraint(name = "uk_function_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Function extends BaseBO {

    @NotEmpty
    @Column(nullable = false)
    private String name;

// TODO:"Add app name to member, so we can have functions per app and only show them
//    @NotEmpty
//    @Column(nullable = false)
//    private String appName;

}