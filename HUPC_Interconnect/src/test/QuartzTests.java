package test;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTests implements Job {
	static Log log = LogFactory.getLog("test");
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("./ini/log4j.properties");
		log.info("Command line arguments:");
		
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			Date runTime = TriggerUtils.getEvenMinuteDate(new Date());
			
			JobDetail job = new JobDetail("job1", "group1", QuartzTests.class);
			SimpleTrigger trigger = new SimpleTrigger("trigger1", "group1", runTime);
			sched.scheduleJob(job, trigger);
			log.info(job.getFullName() + " will run at: " + runTime);  
			
			sched.start();
			
			try {
				Thread.sleep(90L * 1000L); 
			} catch (Exception e) {
			}

			log.info("------- Shutting Down ---------------------");
			sched.shutdown(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("------- Executing ---------------------");
	}

}
