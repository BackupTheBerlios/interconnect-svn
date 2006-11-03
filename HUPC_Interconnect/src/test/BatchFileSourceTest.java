package test;

import java.util.ArrayList;

import org.apache.log4j.PropertyConfigurator;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import de.hupc.jmx.Management;
import de.hupc.sources.BatchFileSource;
import de.hupc.sources.BatchFileSourceListener;

public class BatchFileSourceTest implements BatchFileSourceListener  {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("./ini/log4j.properties");
		try {			
			Management m = new Management();
			m.initJMXManagement();
			Scheduler sched = new StdSchedulerFactory().getScheduler();
			JobDetail job = new JobDetail("job1", "group1", BatchFileSource.class);
			job.getJobDataMap().put("directory", "c:/temp/polling");
			job.getJobDataMap().put("filenamePattern", ".*(txt)$");
			job.getJobDataMap().put("listener", new BatchFileSourceTest());
			Trigger trigger = new CronTrigger("trigger1", "group1", "0/1 * * * * ?");			
			sched.scheduleJob(job, trigger);			
			sched.start();
			while(true) {
				Thread.sleep(10);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void filesAvailable(ArrayList<String> fileNames) {
		System.out.println("New files there :-)");
		for (String name : fileNames) {
			System.out.println(name);
		}
		
		// test blocking behaviour
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
}
