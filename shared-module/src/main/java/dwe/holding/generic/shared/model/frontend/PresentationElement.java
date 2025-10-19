package dwe.holding.generic.shared.model.frontend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresentationElement {
    public Long id;
    public String name;
    public Boolean connected = true;
}