package com.quartz.timeparse;

import net.sf.json.JSONObject;

public class TimerParser {
	
	public static String timeParser(String timer) throws Exception{
		String parsedTime = null;
		JSONObject timerRule = JSONObject.fromObject(timer);
		String type = timerRule.getString("type");
		switch (type) {
		case "pointtime":
			parsedTime = parseCronTimerRule(timerRule);
			break;
		case "periodictime":
			parsedTime = parsePeriodicTimeRule(timerRule);
			break;
		default:
			break;
		}
		return parsedTime;
	}
	
	/**
	 * 解析cron表达式
	 * @param timerRule
	 * @return
	 * @throws BusinessException
	 */
	public static String parseCronTimerRule(JSONObject timerRule) throws Exception {
		return PointTimeParser.parseCronTimerRule(timerRule);
	}
	
	/**
	 * 解析周期执行时间，以秒为单位
	 * @param timerRule
	 * @return
	 */
	public static String parsePeriodicTimeRule(JSONObject timerRule){
		return PeriodicTimeParser.parsePeriodicTimeRule(timerRule).toString();
	}
}
