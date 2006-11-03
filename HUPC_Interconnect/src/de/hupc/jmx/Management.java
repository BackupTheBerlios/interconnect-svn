package de.hupc.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import de.hupc.util.FileUtil;

public class Management implements ManagementMBean {
	public void initJMXManagement() throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("de.hupc.jmx:type=Management");
		Management m = new Management();
		mbs.registerMBean(m, name);
	}
	
	public String sayHello() {
		return "Hello";
	}

	public String getLog(int lines) {
		FileUtil fu = new FileUtil();
		String log = null;
		
		try {
			log = fu.readFile("./log/interconnect.log");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return log;
	}

}
