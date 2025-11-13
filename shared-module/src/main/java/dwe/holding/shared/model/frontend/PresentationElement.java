package dwe.holding.shared.model.frontend;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PresentationElement {
    public Long id;
    public String name;
    public Boolean connected = true;

    public PresentationElement(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public PresentationElement(Long id, String name, Boolean connected) {
        this.id = id;
        this.name = name;
        this.connected = connected;
    }

    public PresentationElement() {
    }
}