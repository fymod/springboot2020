package com.fymod.task;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FirstTask {

	@Scheduled(fixedRate = 5000)
	public void schedule1CronTrigger() {
		System.out.println("======5s运行一次=======" + new Date());
	}
	
}
