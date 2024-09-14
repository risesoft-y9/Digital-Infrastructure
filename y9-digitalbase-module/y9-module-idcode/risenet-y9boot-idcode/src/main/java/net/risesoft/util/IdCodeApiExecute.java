package net.risesoft.util;

import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IdCodeApiExecute<T> {

    public Map<?, ?> objectToMap(Object obj) {
        if (obj == null)
            return null;

        return new BeanMap(obj);
    }

    public T execute(Class<T> t, Object obj, String url, boolean post) {
        IdCodeApiService impl = new IdCodeApiService();
        try {
            String resultJson = null;
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>)objectToMap(obj);
            if (post) {
                resultJson = impl.post(url, params);
            } else {
                resultJson = impl.get(url, params);
            }
            ObjectMapper mapper = new ObjectMapper();
            T result = mapper.readValue(resultJson, t);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public T execute(Class<T> t, Map<String, Object> params, String url, boolean post) {
        IdCodeApiService impl = new IdCodeApiService();
        try {
            String resultJson = null;
            if (post) {
                resultJson = impl.post(url, params);
            } else {
                resultJson = impl.get(url, params);
            }
            ObjectMapper mapper = new ObjectMapper();
            T result = mapper.readValue(resultJson, t);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
