package y9.service;

import org.apereo.cas.authentication.credential.AbstractCredential;

public interface Y9LoginUserService {

	void save(AbstractCredential credential, String success, String logMessage);

}
