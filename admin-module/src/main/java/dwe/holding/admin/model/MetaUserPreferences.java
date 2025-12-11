package dwe.holding.admin.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Table(name = "ADMIN_USER_PREFERENCES")
@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class MetaUserPreferences extends MemberBaseBO {

    @Lob
    String preferencesJson = "{}";  // application-specific preferences for a user

    @OneToOne
    @MapsId   // <-- This makes UserPreferences.id = User.id
    @JoinColumn(name = "id")
    private User user;

    public MetaUserPreferences() {
    }
}