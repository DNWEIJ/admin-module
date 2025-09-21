package dwe.holding.generic.admin.autorisation.function_role;

import dwe.holding.generic.admin.model.base.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresentationFunction extends ToString {
    Long id;
    String name;
    Boolean connected = true;
}