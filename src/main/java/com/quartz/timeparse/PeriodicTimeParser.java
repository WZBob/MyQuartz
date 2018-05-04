package com.quartz.timeparse;
/**
 * 周期执行
 */
import com.quartz.util.QuartzUtil;
import net.sf.json.JSONObject;

public class PeriodicTimeParser{

	/**
	 * 解析周期执行时间，以秒为单位
	 * @param timerRule
	 * @return
	 */
	public static Integer parsePeriodicTimeRule(JSONObject timerRule){
		JSONObject timeData = timerRule.getJSONObject("timedata");
		int seconds = 0;
		if(QuartzUtil.containsKey(timeData, "second")){
			seconds = Integer.parseInt(timeData.getString("second"));
		}
		if(QuartzUtil.containsKey(timeData, "minitue")){
			seconds += Integer.parseInt(timeData.getString("minitue"))*60;
		}
		if(QuartzUtil.containsKey(timeData, "hour")){
			seconds += Integer.parseInt(timeData.getString("hour"))*60*60;
		}
		return seconds;
	}

}
