package org.apereo.cas.web.y9.y9user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.StringUtils;

/**
 * originalID parentID 属性名跟数据库字段间非标准的驼峰转换，导致 BeanPropertyRowMapper 不能正确设入这些属性，需特殊处理
 */
@Deprecated
public class Y9UserRowMapper extends BeanPropertyRowMapper<Y9User> {

    public Y9UserRowMapper() {
        super(Y9User.class);
    }

    @Override
    protected String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }

        if ("originalID".equals(name)) {
            return "original_id";
        } else if ("parentID".equals(name)) {
            return "parent_id";
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isUpperCase(c)) {
                    result.append('_').append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }

    }
}
