package net.risesoft.oidc.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError(Model model, HttpServletRequest request) {
		String errorMessage = getErrorMessage(request);
		if (errorMessage.startsWith("[access_denied]")) {
			model.addAttribute("errorTitle", "Access Denied");
			model.addAttribute("errorMessage", "You have denied access.");
		} else {
			model.addAttribute("errorTitle", "Error");
			model.addAttribute("errorMessage", errorMessage);
		}
		return "error";
	}

	private String getErrorMessage(HttpServletRequest request) {
		String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		return StringUtils.hasText(errorMessage) ? errorMessage : "";
	}

}
