package com.quartz.util;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONObject;

/**
 * @author wangzhb2
 * @data 2018年4月2日
 * @return
 */
public class QuartzUtil {

	// 判断是对应key，value是否为空
	public static boolean containsKey(JSONObject time, String key){
		if(time.containsKey(key) ){
			String value = time.getString(key);
			if(StringUtils.isNotEmpty(value) && (!"null".equals(value) || !"Null".equals(value) || !"NULL".equals(value))){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static boolean isEmpty(String value){
		if(StringUtils.isNotEmpty(value) && (!"null".equals(value) && !"Null".equals(value) && !"NULL".equals(value))){
			return false;
		} else {
			return true;
		}
	}
}
