package net.risesoft.demo.sso.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AppController {

	@RequestMapping("/test")
	public String test() throws IOException {
		return "test";
	}

}
