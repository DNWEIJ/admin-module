package dwe.holding.generic.admin.authorisation;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public void handleError(HttpServletResponse response) throws IOException {
        int status = response.getStatus();
        if (status == 401 || status == 403) {
            response.sendRedirect("/login");
        } else {
            response.sendRedirect("/index"); // fallback for other errors
        }
    }
}