package org.apereo.cas.web.y9.service;

import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;

public interface SaveLoginInfoService {

    /**
     * 包村人员登录信息
     * 
     * @param credential
     * @param success
     * @param logMessage
     * @param screenResolution
     * @param request
     */
    void SaveLoginInfo(final RememberMeUsernamePasswordCredential credential, String success, String logMessage);
}
