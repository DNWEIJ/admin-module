package dwe.holding.admin.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Table(name = "ADMIN_LOCALMEMBER_PREFERENCES")
@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class MetaLocalMemberPreferences extends MemberBaseBO {

    @Lob
    String preferencesJson = "{}";

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private LocalMember localMember;

    public MetaLocalMemberPreferences() {
    }
}