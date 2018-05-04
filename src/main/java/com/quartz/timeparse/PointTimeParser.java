package com.quartz.timeparse;

import com.quartz.util.QuartzUtil;

/**
 * 固定点时间解析
 */
import net.sf.json.JSONObject;

public class PointTimeParser{
	
	/**
	 * 解析cron表达式
	 * @param timerRule
	 * @return
	 * @throws BusinessException
	 */
	public static String parseCronTimerRule(JSONObject timerRule) throws Exception {
		String cron = "";
		String option = timerRule.getString("option");
		JSONObject timeData = timerRule.getJSONObject("timedata");
		switch (option){
		case "day":
			cron = dayParse(timeData);
			break;
		case "week":
			cron = weekParse(timeData);
			break;
		case "month":
			cron = monthParse(timeData);
			break;
		}
		return cron;
	}
	//0 0 10 * * ？  每天上午10点
	private static String dayParse(JSONObject timeData){
		StringBuilder sb = new StringBuilder(hmsParser(timeData));
		sb.append("* * ?");
		return sb.toString();
	}
	//0 0 10 ? * 4   每周三上午10点
	private static String weekParse(JSONObject timeData) throws Exception {
		if(!QuartzUtil.containsKey(timeData, "week")){
			throw new Exception("按周定时，周几不能为空！");
		}
		StringBuilder sb = new StringBuilder(hmsParser(timeData));
		int weekday = Integer.parseInt(timeData.getString("week"));
		int localWeekday = (weekday+1)%7;
		sb.append("? * "+localWeekday);
		return sb.toString();
	}
	//0 0 10 15 * ？  每月15号10点
	private static String monthParse(JSONObject timeData) throws Exception {
		if(!QuartzUtil.containsKey(timeData, "date")){
			throw new Exception("按月定时，日期不能为空！");
		}
		StringBuilder sb = new StringBuilder(hmsParser(timeData));
		int date = Integer.parseInt(timeData.getString("date"));
		if(date == -1){
			sb.append("L * ?");
		} else {
			sb.append(date+" * ?");
		}
		return sb.toString();
	}

	private static String hmsParser(JSONObject timeData){
		StringBuilder sb = new StringBuilder();
		sb.append(hmsParserByKey(timeData, "second"));
		sb.append(hmsParserByKey(timeData, "minitue"));
		sb.append(hmsParserByKey(timeData, "hour"));
		return sb.toString();
	}
	
	private static String hmsParserByKey(JSONObject timeData, String key){
		String result = "* ";
		if(QuartzUtil.containsKey(timeData, key)){
			String value = timeData.getString(key);
//			if(Integer.parseInt(value) != 0){
//				result = Integer.parseInt(value) +" ";
//			}
			result = Integer.parseInt(value) +" ";
		}
		return result;
	}
}
