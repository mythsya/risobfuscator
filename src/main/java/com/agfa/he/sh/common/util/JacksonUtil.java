package com.agfa.he.sh.common.util;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {

	public final static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	/**
	 * 
	 * @param jsonStr
	 * @param valueType
	 * @return
	 */
	public static <T> T readValue(String jsonStr, Class<T> valueType) {
		try {
			return objectMapper.readValue(jsonStr, valueType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param jsonStr
	 * @param valueTypeRef
	 * @return
	 */
	public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {
		try {
			return objectMapper.readValue(jsonStr, valueTypeRef);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
