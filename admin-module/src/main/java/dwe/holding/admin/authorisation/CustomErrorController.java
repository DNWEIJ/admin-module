package dwe.holding.admin.authorisation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@Slf4j
public class CustomErrorController { // implements ErrorController {

    // @RequestMapping("/error")
    public void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("/error on: {}", request.getRequestURI());
        int status = response.getStatus();
        if (status == 401 || status == 403) {
            response.sendRedirect("/admin/login");
        } else {
            response.sendRedirect("/admin/index"); // fallback for other errors
        }
    }
}