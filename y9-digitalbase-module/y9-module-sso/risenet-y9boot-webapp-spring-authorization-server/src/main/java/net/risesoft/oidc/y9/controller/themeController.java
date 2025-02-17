package net.risesoft.oidc.y9.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.oidc.y9.entity.Y9Theme;
import net.risesoft.oidc.y9.repository.Y9ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class themeController {

	@Autowired
	private Y9ThemeRepository y9ThemeRepository;

	@GetMapping("/theme/list")
	public List<Y9Theme> list() {
		List<Y9Theme> y9Themes = y9ThemeRepository.findAll();
		return y9Themes;
	}

}
