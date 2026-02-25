package dwe.holding.admin.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;

public class ControllerHelper {

    public static boolean getHtmxAndAddToModel(HttpServletRequest request, Model model) {
        final boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        model.addAttribute("isHtmx", isHtmx);
        return isHtmx;
    }
}
