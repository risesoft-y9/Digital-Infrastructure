package org.apereo.cas.web.y9.service;

import org.apereo.cas.authentication.credential.Y9Credential;

public interface Y9LoginUserService {

    void save(final Y9Credential credential, String success, String logMessage);

}
