package dwe.holding.reporting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class GenerateThymeleafService {

    @Autowired
    @Qualifier("emailBackEndTemplateEngine")
    private TemplateEngine templateEngine;

    public String generateFromTemplate(String template, Object data) {
        Context context = new Context();
        context.setVariable("data", data);
        return templateEngine.process(template, context);
    }
}