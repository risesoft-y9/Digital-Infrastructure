package y9.service;

import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;

public interface Y9LoginUserService {

    void save(final RememberMeUsernamePasswordCredential credential, String success, String logMessage);

}
