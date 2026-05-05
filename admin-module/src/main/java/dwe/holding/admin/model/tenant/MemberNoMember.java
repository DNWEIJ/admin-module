package dwe.holding.admin.model.tenant;


import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "ADMIN_MEMBER")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
// This is the readonly member for validating the account before being logged in; no memberId available in securty context, so no member available on this domain object
public class MemberNoMember extends BaseBO {

    @Column(nullable = false)
    private String shortCode;
    @Column(nullable = false)
    @NotEmpty
    private String password;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum active;
}
