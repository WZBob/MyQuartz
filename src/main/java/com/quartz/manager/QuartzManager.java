package com.quartz.manager;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.job.entity.SchedulJobVO;
import com.quartz.timeparse.PointTimeParser;
import com.quartz.timeparse.PeriodicTimeParser;

import net.sf.json.JSONObject;

public class QuartzManager {

	private static Logger logger = LoggerFactory.getLogger(QuartzManager.class);
	private static String JOB_NAME = "TIMETASK_JOBNAME";
	private static String JOB_GROUP_NAME = "TIMETASK_JOBGROUOP";
	private static String JOB_DESCRIPTION = "TIMETASK_JOBDESC";
	private static String TRIGGER_NAME = "TIMETASK_TRIGGERNAME";
	private static String TRRIGGER_GROUP_NAME = "TIMETASK_TRIGGERGROUOP";
	private static String TRIGGER_DESCRIPTION = "TIMETASK_TRIGGERDESC";

	private Scheduler scheduler;

	// 添加任务
	public void addJob(SchedulJobVO jobVO) throws Exception {
		if (jobVO.getClazz() == null) {
			throw new Exception("任务类为空！");
		}
		if (jobVO.getSchedulTime() == null) {
			throw new Exception("任务执行时间为空！");
		}
		if (scheduler.checkExists(new JobKey(jobVO.getJobName(), jobVO.getJobGroupName()))) {
			return;
		}
		
		JobDetail jobDetail = this.createJobDetail(jobVO);
		addCustomerData(jobDetail, jobVO);
		
		Trigger trigger = createTrriger(jobVO);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
			logger.info("定时任务添加成功！jobName: " + jobVO.getJobName() + " jobGroupName: " + jobVO.getJobGroupName());
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}

	}

	// 更新和保存
	public void updateJob(SchedulJobVO jobVO) throws Exception {
		if (checkExists(jobVO)) {
			modifyJobTime(jobVO);
		} else {
			addJob(jobVO);
		}
	}
	
	//更新任务
	public void modifyJobTime(SchedulJobVO jobVO) throws Exception {
		if (jobVO.getTriggerName() == null) {
			throw new Exception("触发器名为空！");
		}
		if (jobVO.getSchedulTime() == null) {
			throw new Exception("触发器时间为空！");
		}
		TriggerKey triggerKey = new TriggerKey(jobVO.getTriggerName(), jobVO.getTriggerGroupName());
		Trigger trigger;
		try {
			trigger = scheduler.getTrigger(triggerKey);
			if (trigger == null) {
				throw new Exception("未找到触发器：" + jobVO.getTriggerName());
			}

			JSONObject timerRule = JSONObject.fromObject(jobVO.getSchedulTime());
			String type = timerRule.getString("type");
			if ("pointtime".equals(type)) {
				String newCronExpression = PointTimeParser.parseCronTimerRule(timerRule);
				CronTrigger cronTrigger = null;
				if(trigger instanceof SimpleTrigger){
					cronTrigger = this.updateCronTrigger(jobVO, newCronExpression);
					scheduler.rescheduleJob(triggerKey, cronTrigger);
				} else {
					cronTrigger = (CronTrigger) trigger;
					String oldCronExpression = cronTrigger.getCronExpression();
					if (!oldCronExpression.equals(newCronExpression)) {
						cronTrigger = this.updateCronTrigger(jobVO, newCronExpression);
						scheduler.rescheduleJob(triggerKey, cronTrigger);
					}
				}
			} else if ("periodictime".equals(type)) {
				Integer newIntervalInSeconds = PeriodicTimeParser.parsePeriodicTimeRule(timerRule);
				SimpleTrigger simpleTrigger = null;
				if(trigger instanceof CronTrigger){
					simpleTrigger = this.updateSimpleTrigger(jobVO, newIntervalInSeconds);
					scheduler.rescheduleJob(triggerKey, simpleTrigger);
				} else {
					simpleTrigger = (SimpleTrigger) trigger;
					long oldIntervalInSeconds = simpleTrigger.getRepeatInterval();
					if (oldIntervalInSeconds != newIntervalInSeconds) {
						simpleTrigger = this.updateSimpleTrigger(jobVO, newIntervalInSeconds);
						scheduler.rescheduleJob(triggerKey, simpleTrigger);
					}
				}
			} else {
				throw new Exception("定时规则不正确，请检查是定时还是周期执行！");
			}
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
		
	}

	public void removeJob(SchedulJobVO jobVO) throws Exception {
		if (jobVO.getTriggerName() == null) {
			throw new Exception("触发器名为空！");
		}
		if (jobVO.getJobName() == null) {
			throw new Exception("任务名为空！");
		}
		if (!checkExists(jobVO)) {
			return;
		}
		// if (scheduler.checkExists(new JobKey(jobVO.getJobName(),
		// jobVO.getJobGroupName()))) {
		// return;
		// }
		try {
			TriggerKey triggerKey = new TriggerKey(jobVO.getTriggerName(), jobVO.getTriggerGroupName());
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(new JobKey(jobVO.getJobName(), jobVO.getJobGroupName()));
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}

	public boolean checkExists(SchedulJobVO jobVO) throws Exception {
		if (jobVO.getTriggerName() == null) {
			throw new Exception("触发器名为空！");
		}
		if (jobVO.getJobName() == null) {
			throw new Exception("任务名为空！");
		}

		JobKey jobKey = new JobKey(jobVO.getJobName(), jobVO.getJobGroupName());
		try {
			return scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	// 暂停任务
	public void pauseJob(SchedulJobVO jobVO) throws Exception {
		try {
			JobKey jobKey = new JobKey(jobVO.getJobName(), jobVO.getJobGroupName());
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
		
	}

	// 恢复任务
	public void resumeJob(SchedulJobVO jobVO) throws Exception {
		JobKey jobKey = new JobKey(jobVO.getJobName(), jobVO.getJobGroupName());
		try {
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	public void startJobs() throws Exception {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	public void shutdownJobs() throws Exception {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	// 暂停所有任务
	public void pauseAll() throws Exception {
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	// 恢复所有任务
	public void resumeAll() throws Exception {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new Exception(e.getMessage());
		}
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	

	private JobDetail createJobDetail(SchedulJobVO jobVO) {
		JobBuilder jobBuilder = JobBuilder.newJob(jobVO.getClazz());
		String jobDescription = JOB_DESCRIPTION;
		String jobName = JOB_NAME;
		String jobGroupName = JOB_GROUP_NAME;
		// 任务描述
		if (StringUtils.isNotEmpty(jobVO.getJobDescription())) {
			jobDescription = jobVO.getJobDescription();
		}
		if (StringUtils.isNotEmpty(jobVO.getJobName())) {
			jobName = jobVO.getJobName();
		}
		if (StringUtils.isNotEmpty(jobVO.getJobGroupName())) {
			jobGroupName = jobVO.getJobGroupName();
		}
		// 描述
		jobBuilder.withDescription(jobDescription);
		// 任务名 组名
		jobBuilder.withIdentity(jobName, jobGroupName);
		return jobBuilder.build();
	}
	
	private void addCustomerData(JobDetail jobDetail, SchedulJobVO jobVO){
		JSONObject customerData = jobVO.getCustomerData();
		if(customerData == null || customerData.isNullObject() || customerData.isEmpty()){
			return;
		}
		JobDataMap dataMap = jobDetail.getJobDataMap();
		for (Object key : customerData.keySet()) {//key不会为null
			Object value = customerData.get(key.toString());
			dataMap.put(key.toString(), value);
		}
	}

	private Trigger createTrriger(SchedulJobVO jobVO) throws Exception {
		Trigger trigger = null;
		JSONObject timerRule = JSONObject.fromObject(jobVO.getSchedulTime());
		String type = timerRule.getString("type");
		if ("pointtime".equals(type)) {
			trigger = this.createCronTrigger(jobVO);
		} else if ("periodictime".equals(type)) {
			trigger = this.createSimpleTrigger(jobVO);
		} else {
			throw new Exception("定时规则不正确，请检查是定时还是周期执行！");
		}
		return trigger;
	}
	
	private CronTrigger createCronTrigger(SchedulJobVO jobVO) throws Exception {
		JSONObject timerRule = JSONObject.fromObject(jobVO.getSchedulTime());
		String cronExpression = PointTimeParser.parseCronTimerRule(timerRule);
		logger.info("定时规则："+cronExpression);
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
		this.buildTrigger(jobVO, triggerBuilder);
		return triggerBuilder.build();
	}

	private CronTrigger updateCronTrigger(SchedulJobVO jobVO, String newCronExpression) throws Exception {
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression));
		triggerBuilder.withDescription(jobVO.getTriggerDescription());
		triggerBuilder.withIdentity(jobVO.getTriggerName(), jobVO.getTriggerGroupName());
		triggerBuilder.startNow();
		return triggerBuilder.build();
	}

	private SimpleTrigger createSimpleTrigger(SchedulJobVO jobVO) {
		JSONObject timerRule = JSONObject.fromObject(jobVO.getSchedulTime());
		Integer intervalInSeconds = PeriodicTimeParser.parsePeriodicTimeRule(timerRule);
		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalInSeconds).repeatForever());
		this.buildTrigger(jobVO, triggerBuilder);
		return triggerBuilder.build();
	}

	private SimpleTrigger updateSimpleTrigger(SchedulJobVO jobVO, int newIntervalInSeconds) {
		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(newIntervalInSeconds).repeatForever());
		triggerBuilder.withDescription(jobVO.getTriggerDescription());
		triggerBuilder.withIdentity(jobVO.getTriggerName(), jobVO.getTriggerGroupName());
		triggerBuilder.startNow();
		return triggerBuilder.build();
	}

	private void buildTrigger(SchedulJobVO jobVO, TriggerBuilder<? extends Trigger> triggerBuilder) {
		String triggerDescription = TRIGGER_DESCRIPTION;
		String triggerName = TRIGGER_NAME;
		String triggerGroupName = TRRIGGER_GROUP_NAME;
		// 任务描述
		if (StringUtils.isNotEmpty(jobVO.getJobDescription())) {
			triggerDescription = jobVO.getTriggerDescription();
		}
		if (StringUtils.isNotEmpty(jobVO.getJobName())) {
			triggerName = jobVO.getTriggerName();
		}
		if (StringUtils.isNotEmpty(jobVO.getJobGroupName())) {
			triggerGroupName = jobVO.getTriggerGroupName();
		}

		// 触发器描述
		triggerBuilder.withDescription(triggerDescription);

		// 触发器名 组名
		triggerBuilder.withIdentity(triggerName, triggerGroupName);

		// 触发器开始时间
		if (jobVO.getStartTime() != null) {
			triggerBuilder.startAt(jobVO.getStartTime());
		} else {
			triggerBuilder.startNow();
		}
	}
}
