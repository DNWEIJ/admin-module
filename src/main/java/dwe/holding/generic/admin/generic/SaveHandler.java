package dwe.holding.generic.admin.generic;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Function;

@Component
public class SaveHandler {

    private final RequestMappingHandlerMapping handlerMapping;

    public SaveHandler(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public <F> String handleSave(
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirect,
            HttpServletRequest request,
            Function<F, Long> processor,
            F form
    ) {
        SaveConfig config = resolveConfig(request);

        Long entityId = 0L;

        if (isNew(request)) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("errors", bindingResult.getAllErrors());
                return config.errorView();
            }

            entityId = processor.apply(form);
            redirect.addFlashAttribute("message", config.successMessage());
        }

        return buildRedirect(entityId, config.redirectBase());
    }

    // This replaces getRedirectForNew(request)
    private boolean isNew(HttpServletRequest request) {
        // Centralize logic here, e.g., check request param
        String newFlag = request.getParameter("new");
        return "true".equalsIgnoreCase(newFlag);
    }

    private SaveConfig resolveConfig(HttpServletRequest request) {
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handlerMapping.getHandler(request).getHandler();
            SaveConfig config = handlerMethod.getMethodAnnotation(SaveConfig.class);
            if (config == null) {
                throw new IllegalStateException("Missing @SaveConfig annotation on handler method");
            }
            return config;
        } catch (Exception e) {
            throw new IllegalStateException("Could not resolve @SaveConfig for request", e);
        }
    }

    private String buildRedirect(Long id, String baseRedirect) {
        return id != null && id > 0 ? baseRedirect + "/" + id : baseRedirect;
    }
}