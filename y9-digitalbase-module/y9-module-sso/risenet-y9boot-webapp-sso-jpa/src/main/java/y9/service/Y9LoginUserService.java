package y9.service;

import org.apereo.cas.authentication.credential.UsernamePasswordCredential;

public interface Y9LoginUserService {

    void save(final UsernamePasswordCredential credential, String success, String logMessage);

}
