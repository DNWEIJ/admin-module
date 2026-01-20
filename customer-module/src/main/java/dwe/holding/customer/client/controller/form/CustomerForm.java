package dwe.holding.customer.client.controller.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CustomerForm {
    boolean startLastName;
    boolean includeStreetName;
    boolean includeFirstTel;
    boolean includePet;

    public void updateForm(boolean startLastName, boolean includeStreet, boolean includeNameTelephone, boolean includePet) {
        this.startLastName = startLastName;
        this.includeStreetName = includeStreet;
        this.includeFirstTel = includeNameTelephone;
        this.includePet = includePet;
    }
}