package dwe.holding.customer.client.mapper;

import dwe.holding.customer.client.model.Customer;
import dwe.holding.customer.expose.CustomerService;
import dwe.holding.shared.model.type.YesNoEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateCustomerFromForm(Customer form, @MappingTarget Customer customer);

    CustomerService.Customer toCustomer(Customer customer);

    default boolean map(YesNoEnum value) {
        return value == YesNoEnum.Yes;
    }

    default YesNoEnum map(boolean value) {
        return value ? YesNoEnum.Yes : YesNoEnum.No;
    }
}