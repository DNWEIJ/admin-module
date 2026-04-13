package dwe.holding.admin.authorisation.tenant.user.mapper;

import dwe.holding.admin.model.tenant.User;
import dwe.holding.shared.model.type.YesNoEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "localMemberId", ignore = true)
    @Mapping(target = "memberId", ignore = true)
    void updateUserFromForm(User form, @MappingTarget User user);

    User toUser(User user);

    default boolean map(YesNoEnum value) {
        return value == YesNoEnum.Yes;
    }

    default YesNoEnum map(boolean value) {
        return value ? YesNoEnum.Yes : YesNoEnum.No;
    }
}