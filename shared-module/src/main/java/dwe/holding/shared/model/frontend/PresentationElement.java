package dwe.holding.shared.model.frontend;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PresentationElement {
    public String id;
    public String name;
    public Boolean connected = true;
    public String dataField = "";

    public PresentationElement(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public PresentationElement(Long id, String name) {
        this.id = id.toString();
        this.name = name;
    }

    public PresentationElement(Long id, String name, String dataField) {
        this.id = id.toString();
        this.name = name;
        this.dataField = dataField;
    }


    public PresentationElement(Long id, String name, Boolean connected) {
        this.id = id.toString();
        this.name = name;
        this.connected = connected;
    }

    public PresentationElement() {
    }

    public Long getLongId() {
        return Long.parseLong(id);
    }
}