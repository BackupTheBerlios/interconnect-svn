package de.hupc.util;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import de.hupc.connectors.CSVToSQLConnector;
import de.hupc.exception.InterconnectException;

public class Configurator {
	static XMLConfiguration config = null;
	static String pathConfigFile = "./ini/config.xml";
	static Log logger = LogFactory.getLog("de.hupc");
	
	public final static void configure() throws ConfigurationException, SchedulerException, ParseException, InterconnectException {
		logger.debug("configuring connectors ...");
		XMLConfiguration.setDelimiter('|');
		config = new XMLConfiguration();
		config.setFileName(pathConfigFile);
		config.setValidating(false);
		config.load();
		
		// read all configured connectors from configuration file
		List l = config.getList("Connector.Name");		
		if(l.size() == 0) {
			throw(new InterconnectException("No connectors found in config file " + pathConfigFile));
		}
		for (int i=0; i<l.size(); i++) {
			String type = config.getString("Connector(" + i +").Type");
			String name = config.getString("Connector(" + i +").Name");
			logger.debug("Found [" + name + "] of type [" + type + "] connector.");
			if(type.equalsIgnoreCase("CSVToSQL")) {
				addCVSToSQLConnector(i);
			} else {
				throw new InterconnectException("Unknown connector type " + type);
			}
		}
	}

	final static void addCVSToSQLConnector(int num) throws SchedulerException, ParseException {
		// Get configuration values from XML configuration file
		String connectorName = config.getString("Connector(" + num +").Name");
		String jobCronString = config.getString("Connector(" + num +").CronString");
		String bfsPollDirectory = config.getString("Connector(" + num +").BatchFileSource.PollDirectory");
		String bfsFilenamePattern = config.getString("Connector(" + num +").BatchFileSource.FilenamePattern");
		String ssiSQLStatement = config.getString("Connector(" + num +").SQLSink.SQLStatement");		
		String ssiFieldSeperator = config.getString("Connector(" + num +").SQLSink.FieldSeperator");
		String[] attributes = config.getStringArray("Connector(" + num +").SQLSink.Attributes");
		String jdbcServerName = config.getString("Connector(" + num +").SQLSink.JDBC.ServerName");
		String jdbcDatasourceName = config.getString("Connector(" + num +").SQLSink.JDBC.DataSourceName");
		String jdbcDatabaseName = config.getString("Connector(" + num +").SQLSink.JDBC.DatabaseName");
		String jdbcUser = config.getString("Connector(" + num +").SQLSink.JDBC.User");
		String jdbcPassword = config.getString("Connector(" + num +").SQLSink.JDBC.Password");		
		int jdbcPoolMaxConnections = config.getInt("Connector(" + num +").SQLSink.JDBC.PoolMaxConnections");
		int jdbcPoolInitialConnections = config.getInt("Connector(" + num +").SQLSink.JDBC.PoolInitialConnections");
		String jdbcInputNumberLocale = config.getString("Connector(" + num +").SQLSink.JDBC.InputNumberLocale");
		String jdbcSQLNumberLocale = config.getString("Connector(" + num +").SQLSink.JDBC.SQLNumberLocale");
		
		logger.debug("name: " + connectorName);
		logger.debug("cron: " + jobCronString);
		logger.debug("pollDirectory: " + bfsPollDirectory);
		logger.debug("filenamePattern: " + bfsFilenamePattern);
		for (int i = 0; i < attributes.length; i++) {
			logger.debug("attributes["+i+"]: " + attributes[i]);
		}
		logger.debug("ssiSQLStatement: " + ssiSQLStatement);
		logger.debug("ssiFieldSeperator: " + ssiFieldSeperator);
		logger.debug("jdbcServerName: " + jdbcServerName);
		logger.debug("jdbcDatasourceName: " + jdbcDatasourceName);
		logger.debug("jdbcDatabaseName: " + jdbcDatabaseName);
		logger.debug("jdbcUser: " + jdbcUser);
		logger.debug("jdbcPassword: " + jdbcPassword);		
		logger.debug("jdbcPoolMaxConnections: " + jdbcPoolMaxConnections);
		logger.debug("jdbcPoolInitialConnections: " + jdbcPoolInitialConnections);		
		logger.debug("jdbcInputNumberLocale: " + jdbcInputNumberLocale);
		logger.debug("jdbcSQLNumberLocale: " + jdbcSQLNumberLocale);
		
		// create a new CSVToSQLConnector
		CSVToSQLConnector connector = new CSVToSQLConnector();
		connector.setConnectorName(connectorName);
		connector.setJobCronString(jobCronString);
		connector.setAttributes(attributes);
		connector.setBfsFilenamePattern(bfsFilenamePattern);
		connector.setBfsPollDirectory(bfsPollDirectory);
		connector.setSsiFieldSeperator(ssiFieldSeperator);
		connector.setSsiSQLStatement(ssiSQLStatement);
		connector.setJdbcDatabaseName(jdbcDatabaseName);
		connector.setJdbcDatasourceName(jdbcDatasourceName);
		connector.setJdbcPassword(jdbcPassword);
		connector.setJdbcPoolInitialConnections(jdbcPoolInitialConnections);
		connector.setJdbcPoolMaxConnections(jdbcPoolMaxConnections);
		connector.setJdbcServerName(jdbcServerName);
		connector.setJdbcUser(jdbcUser);
		connector.setJdbcInputNumberLocale(jdbcInputNumberLocale);
		connector.setJdbcSQLNumberLocale(jdbcSQLNumberLocale);
		connector.init();
	}
	
	public static XMLConfiguration getConfig() throws Exception {
		if(config == null) {
			configure();
		}
		return config;
	}
}
