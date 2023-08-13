package org.apereo.cas.web.y9;

import java.util.Date;

import org.apereo.cas.services.Y9User;
import org.apereo.cas.web.y9.service.Y9UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {
	@Autowired
	private Y9UserService y9UserService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// System.out.println(event.getApplicationContext().getEnvironment().getProperty("path"));
		Y9User y9User = null;
		try {
			y9User = y9UserService.findById("11111111-1111-1111-1111-111111111117");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (y9User == null) {
			y9User = new Y9User();
			y9User.setId("11111111-1111-1111-1111-111111111117");
			y9User.setPersonId("11111111-1111-1111-1111-111111111117");
			y9User.setParentId("11111111-1111-1111-1111-111111111115");
			y9User.setTenantId("11111111-1111-1111-1111-111111111113");
			y9User.setDn("cn=系统管理员,o=组织");
			y9User.setGlobalManager(true);
			y9User.setGuidPath("11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111117");
			y9User.setLoginName("systemManager");
			y9User.setManagerLevel(1);
			y9User.setName("系统管理员");
			y9User.setOriginal(true);
			y9User.setPassword("Risesoft@2023");
			y9User.setPersonType("Manager");
			y9User.setSex(1);
			y9User.setTenantName("default");
			y9User.setTenantShortName("default");
			y9User.setCreateTime(new Date());
			y9User.setUpdateTime(new Date());
			// y9User.setIdNum(null);
			// y9User.setOriginalId(null);
			// y9User.setEmail(null);
			// y9User.setMobile(null);
			// y9User.setOrderedPath(null);
			// y9User.setPositions(null);
			// y9User.setRoles(null);
			y9UserService.save(y9User);
		}
	}
}
