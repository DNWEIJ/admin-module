package dwe.holding.vmas.thymeleaf;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.thymeleaf.spring6.SpringTemplateEngine;

// TODO see if this even is the correct way of doing it
// @Configuration
public class ThymeleafConfig {

    @Bean
    public SpringTemplateEngine templateEngine(ResourceLoader resourceLoader) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(new MinifyHtmlPostProcessor(resourceLoader));
        return engine;
    }
}