package net.risesoft.oidc.authorization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(String theme, Model model) {
        model.addAttribute("theme", theme);
        if (StringUtils.hasText(theme)) {
            return "theme/login-" + theme;
        }
        return "login";
    }

}
