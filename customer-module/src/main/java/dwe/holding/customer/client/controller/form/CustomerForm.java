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

}