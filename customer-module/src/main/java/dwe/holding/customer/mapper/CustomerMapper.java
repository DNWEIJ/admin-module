package dwe.holding.customer.mapper;

import dwe.holding.customer.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateCustomerFromForm(Customer form, @MappingTarget Customer customer);
}