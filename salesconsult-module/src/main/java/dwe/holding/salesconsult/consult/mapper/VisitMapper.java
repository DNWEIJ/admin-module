package dwe.holding.salesconsult.consult.mapper;

import dwe.holding.salesconsult.consult.model.Visit;
import dwe.holding.shared.model.type.YesNoEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)

    void updateVisitFromForm(Visit form, @MappingTarget Visit visit);


    default boolean map(YesNoEnum value) {
        return value == YesNoEnum.Yes;
    }

    default YesNoEnum map(boolean value) {
        return value ? YesNoEnum.Yes : YesNoEnum.No;
    }
}