package org.apereo.cas.web.y9.service;

/**
 * Y9KeyValueService
 *
 * @author shidaobang
 * @date 2024/04/23
 */
public interface Y9KeyValueService {

    void cleanUpExpiredKeyValue();

    String get(String key);

    void put(String key, String value, long minutes);
}
