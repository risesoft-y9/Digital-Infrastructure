package net.risesoft.y9.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.BeansException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.type.MapType;

/**
 * JSON 工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9JsonUtil {

	public static JsonMapper jsonMapper = JsonMapper.builder()
			.defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))
			.defaultTimeZone(TimeZone.getDefault())
			.configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true)
			.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.build();

	public static JsonMapper getJsonMapper() {
		/*
		if (jsonMapper == null) {
			try {
				jsonMapper = Y9Context.getBean("jacksonJsonMapper");
			} catch (BeansException e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}*/
		return jsonMapper;
	}

	public static <T> T[] readArray(String content, Class<T> valueType) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content, jsonMapper.getTypeFactory().constructArrayType(valueType));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static HashMap<String, Object> readHashMap(String content) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <K, V> HashMap<K, V> readHashMap(String content, Class<K> keyClass, Class<V> valueClass) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <T> List<T> readList(String content, Class<T> valueType) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, valueType));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static List<Map<String, Object>> readListOfMap(String content) {
		try {
			//jsonMapper = getJsonMapper();
			MapType javaType = jsonMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, javaType));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <K, V> List<Map<K, V>> readListOfMap(String content, Class<K> keyClass, Class<V> valueClass) {
		try {
			//jsonMapper = getJsonMapper();
			MapType javaType = jsonMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, javaType));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T readValue(String content, Class<T> valueType) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content, valueType);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T readValue(String content, JavaType valueType) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content, valueType);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content, valueTypeRef);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static <T> Y9Result<T> readY9Result(String content, Class<T> valueType) {
		try {
			//jsonMapper = getJsonMapper();
			return jsonMapper.readValue(content,
					jsonMapper.getTypeFactory().constructParametricType(Y9Result.class, valueType));
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		} catch (BeansException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	public static String writeValueAsString(Object value) {
		String s = "";
		try {
			//jsonMapper = getJsonMapper();
			s = jsonMapper.writeValueAsString(value);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return s;
	}

	public static String writeValueAsString(Object value, boolean include, String... filters) {
		FilterProvider filterProvider = null;
		//jsonMapper = getJsonMapper();
		if (include) {
			filterProvider = new SimpleFilterProvider().addFilter("propertyFilter",
					SimpleBeanPropertyFilter.filterOutAllExcept(filters));
		} else {
			filterProvider = new SimpleFilterProvider().addFilter("propertyFilter",
					SimpleBeanPropertyFilter.serializeAllExcept(filters));
		}
		ObjectWriter writer = jsonMapper.writer(filterProvider);
		String s = "";
		try {
			s = writer.writeValueAsString(value);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return s;
	}

	public static String writeValueAsStringWithDefaultPrettyPrinter(Object value) {
		String s = "";
		try {
			//jsonMapper = getJsonMapper();
			s = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		} catch (JacksonException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return s;
	}
}
