package de.hupc.connectors;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import de.hupc.sinks.SQLSink;
import de.hupc.sources.BatchFileSource;
import de.hupc.sources.BatchFileSourceListener;
import de.hupc.util.JDBCDatasource;

public class CSVToSQLConnector {
	String connectorName = null;
	String jobCronString = null;
	String bfsPollDirectory = null;
	String bfsFilenamePattern = null;
	String ssiSQLStatement = null;		
	String ssiFieldSeperator = null;
	String[] attributes = null;
	String jdbcServerName = null;
	String jdbcDatasourceName = null;
	String jdbcDatabaseName = null;
	String jdbcUser = null;
	String jdbcPassword = null;
	int jdbcPoolMaxConnections;
	int jdbcPoolInitialConnections;
	String jdbcInputNumberLocale = null;
	String jdbcSQLNumberLocale = null;
	JDBCDatasource dataSource = new JDBCDatasource();
	BFListener bfListener = new BFListener();
	SQLSink sqlSink = null;
	static Log logger = LogFactory.getLog("de.hupc");
	
	public void init() throws SchedulerException, ParseException {
		this.dataSource.setDatabaseName(this.jdbcDatabaseName);
		this.dataSource.setDataSourceName(this.jdbcDatasourceName);
		this.dataSource.setInitialConnections(this.jdbcPoolInitialConnections);
		this.dataSource.setMaxConnections(this.jdbcPoolMaxConnections);
		this.dataSource.setPassword(this.jdbcPassword);
		this.dataSource.setUser(this.jdbcUser);
		
		this.sqlSink = new SQLSink(this.attributes, this.ssiSQLStatement);
		this.sqlSink.setFieldSeperator(this.ssiFieldSeperator);
		this.sqlSink.setDataSource(this.dataSource);
		this.sqlSink.setInputNumberFormat(new Locale(this.jdbcInputNumberLocale));
		this.sqlSink.setSQLNumberFormat(new Locale(this.jdbcSQLNumberLocale));
		
		Scheduler sched = new StdSchedulerFactory().getScheduler();
		JobDetail job = new JobDetail(this.connectorName, "group1", BatchFileSource.class);
		job.getJobDataMap().put("directory", this.bfsPollDirectory);
		job.getJobDataMap().put("filenamePattern", this.bfsFilenamePattern);
		job.getJobDataMap().put("listener", this.bfListener);
		Trigger trigger = new CronTrigger("trigger1", "group1", this.jobCronString);
		sched.scheduleJob(job, trigger);
		sched.start();
	}
	
	void insertData(String filePath) throws Exception {
		this.sqlSink.startProcessing(filePath);
	}
	
	class BFListener implements BatchFileSourceListener  {
		public void filesAvailable(ArrayList<String> fileNames) {
			int i=0;			
			for (String name : fileNames) {				
				try {
					i++;					
					insertData(name);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	public String[] getAttributes() {
		return attributes;
	}
	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}
	public String getBfsFilenamePattern() {
		return bfsFilenamePattern;
	}
	public void setBfsFilenamePattern(String bfsFilenamePattern) {
		this.bfsFilenamePattern = bfsFilenamePattern;
	}
	public String getBfsPollDirectory() {
		return bfsPollDirectory;
	}
	public void setBfsPollDirectory(String bfsPollDirectory) {
		this.bfsPollDirectory = bfsPollDirectory;
	}
	public String getConnectorName() {
		return connectorName;
	}
	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}
	public String getJdbcDatabaseName() {
		return jdbcDatabaseName;
	}
	public void setJdbcDatabaseName(String jdbcDatabaseName) {
		this.jdbcDatabaseName = jdbcDatabaseName;
	}
	public String getJdbcDatasourceName() {
		return jdbcDatasourceName;
	}
	public void setJdbcDatasourceName(String jdbcDatasourceName) {
		this.jdbcDatasourceName = jdbcDatasourceName;
	}
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	public int getJdbcPoolInitialConnections() {
		return jdbcPoolInitialConnections;
	}
	public void setJdbcPoolInitialConnections(int jdbcPoolInitialConnections) {
		this.jdbcPoolInitialConnections = jdbcPoolInitialConnections;
	}
	public int getJdbcPoolMaxConnections() {
		return jdbcPoolMaxConnections;
	}
	public void setJdbcPoolMaxConnections(int jdbcPoolMaxConnections) {
		this.jdbcPoolMaxConnections = jdbcPoolMaxConnections;
	}
	public String getJdbcServerName() {
		return jdbcServerName;
	}
	public void setJdbcServerName(String jdbcServerName) {
		this.jdbcServerName = jdbcServerName;
	}
	public String getJdbcUser() {
		return jdbcUser;
	}
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}
	public String getJobCronString() {
		return jobCronString;
	}
	public void setJobCronString(String jobCronString) {
		this.jobCronString = jobCronString;
	}
	public String getSsiFieldSeperator() {
		return ssiFieldSeperator;
	}
	public void setSsiFieldSeperator(String ssiFieldSeperator) {
		this.ssiFieldSeperator = ssiFieldSeperator;
	}
	public String getSsiSQLStatement() {
		return ssiSQLStatement;
	}
	public void setSsiSQLStatement(String ssiSQLStatement) {
		this.ssiSQLStatement = ssiSQLStatement;
	}

	public String getJdbcInputNumberLocale() {
		return jdbcInputNumberLocale;
	}

	public void setJdbcInputNumberLocale(String jdbcInputNumberLocale) {
		this.jdbcInputNumberLocale = jdbcInputNumberLocale;
	}

	public String getJdbcSQLNumberLocale() {
		return jdbcSQLNumberLocale;
	}

	public void setJdbcSQLNumberLocale(String jdbcSQLNumberLocale) {
		this.jdbcSQLNumberLocale = jdbcSQLNumberLocale;
	}
}