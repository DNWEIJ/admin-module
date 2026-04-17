package dwe.holding.admin.authorisation.tenant.mapper;

import dwe.holding.admin.model.tenant.LocalMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocalMemberMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "addedOn", ignore = true)
    void updateLocalMemberFromForm(LocalMember form, @MappingTarget LocalMember localMember);
}
