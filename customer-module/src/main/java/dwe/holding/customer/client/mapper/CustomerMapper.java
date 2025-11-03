package dwe.holding.customer.client.mapper;

import dwe.holding.customer.client.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateCustomerFromForm(Customer form, @MappingTarget Customer customer);
}