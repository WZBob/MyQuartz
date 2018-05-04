package com.quartz.job.entity;

import java.util.Date;
import org.quartz.Job;
import net.sf.json.JSONObject;

public class SchedulJobVO {
	private Class<? extends Job> clazz;
	private String jobName;
	private String jobDescription;
	private String jobGroupName;
	private String triggerName;
	private String triggerDescription;
	private String triggerGroupName;
	private String schedulTime;
	private Date startTime;
	private JSONObject customerData;
	
	public Class<? extends Job> getClazz() {
		return clazz;
	}
	public void setClazz(Class<? extends Job> clazz) {
		this.clazz = clazz;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getJobGroupName() {
		return jobGroupName;
	}
	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public String getTriggerDescription() {
		return triggerDescription;
	}
	public void setTriggerDescription(String triggerDescription) {
		this.triggerDescription = triggerDescription;
	}
	public String getTriggerGroupName() {
		return triggerGroupName;
	}
	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}
	
	public String getSchedulTime() {
		return schedulTime;
	}
	public void setSchedulTime(String schedulTime) {
		this.schedulTime = schedulTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public JSONObject getCustomerData() {
		return customerData;
	}
	public void setCustomerData(JSONObject customerData) {
		this.customerData = customerData;
	}
	
}
