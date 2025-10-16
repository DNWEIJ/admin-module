package dwe.holding.generic.admin.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

  


@Table(name = "ADMIN_USER_PREFERENCES")
@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class UserPreferences extends TenantBaseBO {
    String userPreferencesJson = "{}";
      Long userId;

    public UserPreferences() {
    }
}