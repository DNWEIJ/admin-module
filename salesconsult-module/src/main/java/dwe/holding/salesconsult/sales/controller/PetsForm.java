package dwe.holding.salesconsult.sales.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PetsForm {
    private List<FormPet> formPet;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class FormPet {
        String checked;
        Long id;
        String purpose;
    }


}