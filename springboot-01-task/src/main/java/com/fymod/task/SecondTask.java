package com.fymod.task;

import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SecondTask {

	/**
	 * 每天的17点25分执行一次
	 */
	@Scheduled(cron="0 25 17 * * ?")
	public void schedule1CronTrigger() {
		System.out.println("固定时间" + new Date());
	}
	
}
