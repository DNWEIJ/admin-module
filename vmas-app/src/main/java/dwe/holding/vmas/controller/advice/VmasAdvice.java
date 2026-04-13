package dwe.holding.vmas.controller.advice;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.vmas.model.VmasUserPreferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import tools.jackson.databind.ObjectMapper;

@ControllerAdvice
@Slf4j
public class VmasAdvice {
    private final ObjectMapper objectMapper;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public VmasAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ModelAttribute
    void modelAdvice(Model model) {
        try {
            VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class).valid();
            model
                    .addAttribute("color", prefData.getColor())
                    .addAttribute("contextPath", contextPath)
            ;
        } catch (Exception e) {
            // do nothing, user not yet logged in
        }
    }
}

