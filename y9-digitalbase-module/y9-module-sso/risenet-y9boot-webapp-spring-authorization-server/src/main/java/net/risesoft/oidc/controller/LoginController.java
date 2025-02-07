package net.risesoft.oidc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}

}
