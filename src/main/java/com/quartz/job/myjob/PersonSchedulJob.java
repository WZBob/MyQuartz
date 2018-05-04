package com.quartz.job.myjob;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonSchedulJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(PersonSchedulJob.class);
	
	@Autowired
	private PersonService personService;
	
	@Override
	public void execute(JobExecutionContext context) {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String name = dataMap.getString("name");
		int age = dataMap.getInt("age");
		String address = dataMap.getString("address");
		
		//业务逻辑
		logger.info(new Date()+" hello quartz!" +name + " "+ age + " " + address);
		logger.info(personService.food(name));
	}
}
