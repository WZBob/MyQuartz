package com.quartz.job.myjob;

import org.springframework.stereotype.Service;

@Service
public class PersonService {
	
	public String food(String name){
		return name + "该吃饭了！";
	}
}
