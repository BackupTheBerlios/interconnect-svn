package test;

import java.text.ParseException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.SchedulerException;

import de.hupc.exception.InterconnectException;
import de.hupc.util.Configurator;

public class ConfiguratorTester {
	public static void main(String[] args) {		
		try {
			PropertyConfigurator.configure("./ini/log4j.properties");
			Configurator.configure();
			while(true) {
				Thread.sleep(50);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InterconnectException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
