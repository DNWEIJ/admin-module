package dwe.holding.reporting.service;


import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
@Component
public class GenerateMustacheService {
    private final ObjectMapper objectMapper;
    private Mustache.Compiler mustacheCompiler;

    public String generateFromTemplate(String templateContent, Object data) {
        Template mustache = mustacheCompiler.compile(templateContent);
        return mustache.execute(data);
    }

    public String generateUsingPrecompiledFromTemplate(Template mustache, String templateContent, Object data) {
        return mustache.execute(data);
    }

    public Template compileTemplateToUse(String templateContent) {
        return mustacheCompiler.compile(templateContent);
    }
}
