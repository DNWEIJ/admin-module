package dwe.holding.customer;

import dwe.holding.generic.admin.security.InformationObject;

public class CustomerInformationHolder implements InformationObject {

    private CustomerInfo customerInfo;

    public CustomerInformationHolder(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    @Override
    public Object getInformation() {
        return customerInfo;
    }
    @Override
    public void setInformation(Object information) {
        this.customerInfo = (CustomerInfo) information;
    }

    public record CustomerInfo(String customerNameStringForScreen, Long customerId){}
}