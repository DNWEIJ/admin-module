package dwe.holding.generic.admin.model;

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
    public   Long id;
    public String name;
    public Boolean connected = true;
}