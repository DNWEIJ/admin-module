package dwe.holding.customer.client.service;

import dwe.holding.admin.sessionstorage.SessionStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SessionStorageCustomer {
    private final SessionStorage storage;

    public CustomerSettings getCustomer() {
        return read();

    }

    public void setCustomer(CustomerSettings settings) {
        update(settings);
    }

    public void setCustomerId(Long id) {
        CustomerSettings customer = read();
        customer.setId(id);
        update(customer);
    }

    public void setCustomerName(String customerNameWithId) {
        CustomerSettings customer = read();
        customer.setName(customerNameWithId);
        update(customer);
    }

    CustomerSettings read() {
        return storage.getModule("customer", SessionStorageCustomer.CustomerSettings.class).orElse(new CustomerSettings(null, null));
    }

    void update(CustomerSettings updatedCustomer) {
        storage.updateModule("customer", updatedCustomer);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CustomerSettings {
        Long id;
        String name;
    }
}
