package dwe.holding.generic.admin.model;

import dwe.holding.generic.admin.model.base.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresentationFunction extends ToString {
    public UUID id;
    public String name;
    public Boolean connected = true;
}