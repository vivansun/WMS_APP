package com.plusone.pwms.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.plusone.pwms.model.Response;

import java.lang.reflect.Type;

public class GsonUtil {

	private static Gson gson = null;

	static {
		if (gson == null) {
			gson = new GsonBuilder().enableComplexMapKeySerialization() // 当Map的key为复杂对象时,需要开启该方法
					.setPrettyPrinting() // 对结果进行格式化，增加换行
					.disableHtmlEscaping() // 防止特殊字符出现乱码
					.create();
		}
	}

	private GsonUtil() {
	}

	public static String toJson(Object object) {
		String gsonString = null;
		if (gson != null) {
			gsonString = gson.toJson(object);
		}
		return gsonString;
	}

	public static <T> T toBean(String gsonString, Class<T> calss) {
		T t = null;
		if (gson != null) {
			t = gson.fromJson(gsonString, calss);
		}
		return t;
	}

	public static <T> T toBean(String gsonString, Type type) {
		T t = null;
		if (gson != null) {
			t = gson.fromJson(gsonString, type);
		}
		return t;
	}

	public static <T> Response<T> toBeanByTypeToken(String gsonString, Type type) {
		Response<T> t = null;
		if (gson != null) {
			t = gson.fromJson(gsonString, type);
		}
		return t;
	}
}
