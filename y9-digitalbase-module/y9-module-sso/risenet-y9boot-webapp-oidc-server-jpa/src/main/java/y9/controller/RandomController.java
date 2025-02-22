package y9.controller;

import y9.util.Y9Context;
import y9.util.Y9Result;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
public class RandomController {
	
	public RandomController() {
		System.out.println("RandomController created...");
	}

	@ResponseBody
	@GetMapping(value = "/getRandom")
	public Y9Result<Object> getRandom() {
		try {
			return Y9Result.success(Y9Context.getProperty("y9.encryptionRsaPublicKey"), "获取成功");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("获取失败");
		}
	}

}
