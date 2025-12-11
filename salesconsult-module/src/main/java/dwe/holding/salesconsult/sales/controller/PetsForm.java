package dwe.holding.salesconsult.sales.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PetsForm {
    @NotNull
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