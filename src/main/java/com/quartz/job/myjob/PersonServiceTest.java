package com.quartz.job.myjob;

import java.util.Date;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.quartz.job.entity.SchedulJobVO;
import com.quartz.manager.QuartzManager;

import net.sf.json.JSONObject;

public class PersonServiceTest {

	public static void main(String[] args) throws Exception{
		Person person = new Person();
		person.setName("bob");
		person.setAge(20);
		person.setAddress("北京");
		
		SchedulJobVO jobVO = new SchedulJobVO();
		jobVO.setClazz(PersonSchedulJob.class);
		jobVO.setJobName("MyJob");
		jobVO.setJobGroupName("MyJobGroup");
		jobVO.setTriggerName("MyTrriger");
		String schedulTime = "{\"type\":\"periodictime\",\"timedata\":{\"hour\":\"0\",\"minitue\":\"0\",\"second\":\"02\"}}";
//		String schedulTime = "{\"type\":\"pointtime\",\"option\":\"day\",\"timedata\":{\"hour\":\"14\",\"minitue\":\"01\",\"second\":\"00\"}}";
		jobVO.setSchedulTime(schedulTime);
		jobVO.setCustomerData(JSONObject.fromObject(person));
		
		long time=  System.currentTimeMillis() + 3*1000L; //3秒后启动任务
	    Date startTime = new Date(time);
	    jobVO.setStartTime(startTime);
	    
	    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
		
	    QuartzManager quartzManager = applicationContext.getBean("quartzManager", QuartzManager.class);
	    quartzManager.addJob(jobVO);
	    
//		QuartzManager quartzManager = new QuartzManager();
//		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//		quartzManager.setScheduler(scheduler);
//		quartzManager.addJob(jobVO);
//		System.out.println("启动时间："+new Date());
	}
}
