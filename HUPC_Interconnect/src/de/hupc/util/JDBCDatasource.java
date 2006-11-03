package de.hupc.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGPoolingDataSource;

public class JDBCDatasource {
	PGPoolingDataSource dataSource = null;
	String serverName = null;
	String dataSourceName = null;
	String databaseName = null;
	String user = null;
	String password = null;
	int maxConnections = 10;
	int initialConnections = 1;
	
	public JDBCDatasource() {
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		
		if(dataSource == null) {
			configureDatasource();
		}
		
		conn = dataSource.getConnection();
		
		return conn;
	}
	
	void configureDatasource() {
		this.dataSource = new PGPoolingDataSource();
		
		// check for mandatory fields
		this.dataSource.setDataSourceName(this.dataSourceName);
		this.dataSource.setServerName(this.serverName);
		this.dataSource.setDatabaseName(this.databaseName);
		this.dataSource.setUser(this.user);
		this.dataSource.setPassword(this.password);
		this.dataSource.setMaxConnections(this.maxConnections);
		this.dataSource.setInitialConnections(this.initialConnections);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public int getInitialConnections() {
		return initialConnections;
	}

	public void setInitialConnections(int initialConnections) {
		this.initialConnections = initialConnections;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
