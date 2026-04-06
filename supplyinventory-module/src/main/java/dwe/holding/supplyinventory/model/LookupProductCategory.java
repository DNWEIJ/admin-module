package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Table(name = "SUPPLY_LOOKUP_COSTING_CATEGORY")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupProductCategory extends MemberBaseBO {
    @Column(nullable = false)
    @NotEmpty
    private String categoryName;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum deleted;

}