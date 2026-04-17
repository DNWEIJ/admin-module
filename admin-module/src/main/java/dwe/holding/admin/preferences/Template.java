package dwe.holding.admin.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Template {
    @JsonProperty("Order")
    private int order;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Text")
    private String text;
    @JsonProperty("Selected")
    private boolean selected;
}
