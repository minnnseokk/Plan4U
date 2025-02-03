package org.project.pack.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {
    @GetMapping("/app/logout")
    public String logoutKakao(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:" + "/app/login";
    }

}
