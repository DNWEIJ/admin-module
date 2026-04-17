package dwe.holding.admin.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TemplatesResponse {
    @JsonProperty("Templates")
    private List<Template> templates;
}


