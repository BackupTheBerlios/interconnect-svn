package de.hupc.sinks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.hupc.exception.InterconnectException;
import de.hupc.util.JDBCDatasource;

public class SQLSink {
	static Log logger = LogFactory.getLog("de.hupc");
	private String[] fileAttributes = null;	
	private String fileSourcePath = null;
	private String jdbcSQLStatement = null;
	private String fieldSeperator = ";";
	private NumberFormat inputNumberFormat = DecimalFormat.getNumberInstance(Locale.GERMANY);
	private NumberFormat sqlNumberFormat = DecimalFormat.getNumberInstance(Locale.US);
	JDBCDatasource dataSource = null;
	
	public SQLSink(String[] pFileAttributes, String pJDBCSQLStatement) {
		this.fileAttributes = pFileAttributes;		
		this.jdbcSQLStatement = pJDBCSQLStatement;
	}
	
	public void startProcessing(String pFileSourcePath) throws Exception {
		this.fileSourcePath = pFileSourcePath;
		this.processSourceFile();
	}
	
	/**
	 * Reads source file line by line and inserts data into database.
	 * @throws  
	 * @throws InterconnectException 
	 * @throws IOException 
	 * @throws Exception
	 */
	private void processSourceFile() throws IOException, InterconnectException {
		int i=0;
		BufferedReader bf = null;
		logger.info("Processing file " + this.fileSourcePath);		
		try {
			// read source file line by line
			bf = new BufferedReader(new FileReader(this.fileSourcePath));
			String line = null;
			while((line = bf.readLine()) != null) {
				String strSQL = this.generateSQLStatement(line);
				this.sendSQL(strSQL);
				i++;
			}
		} finally {
			if(bf != null) {
				bf.close();
			}
		}
		logger.info("File " + this.fileSourcePath + " processed. " + i + " lines inserted into database.");
	}
	
	/**
	 * Generates the SQL statement by substituting placeholders in SQL template with attributes from input line.
	 * @param input
	 * @return
	 * @throws InterconnectException 
	 * @throws Exception
	 */
	private String generateSQLStatement(String input) throws InterconnectException {
		String strSQL = this.jdbcSQLStatement;
		
		String[] fields = input.split(this.fieldSeperator);
		if(fields.length != this.fileAttributes.length) {
			throw(new InterconnectException("Input line \"" + input + "\" does not match expected number of attributes (" + this.fileAttributes.length + ")"));
		}
		for(int i=0; i<fields.length; i++) {
			String field = fields[i].trim();
			logger.debug("[" + this.fileAttributes[i] + "]: Original value: " + field);
			// try to format field if it's a number - don't do it, if field contains alphbetic charcters 
			if(!field.matches(".*\\p{Alpha}.*")) {
				try {
					Number num = inputNumberFormat.parse(field);
					if(num != null) {
						field = sqlNumberFormat.format(num);
						logger.debug("[" + this.fileAttributes[i] + "]: Looks like a number. After conversion: " + field);
					}
				} catch(ParseException pe) {}
			}
			
			strSQL = strSQL.replaceAll("%" + this.fileAttributes[i] + "%", field);
		}
		
		return strSQL;
	}
	
	/**
	 * Sends given SQL statement to database via JDBC.
	 * @param strSQL
	 * @throws Exception
	 */
	private void sendSQL(String strSQL) {
		logger.debug("Sending SQL String: " + strSQL);
		Connection conn = null;
		try {
		    conn = this.dataSource.getConnection();
		    Statement st = conn.createStatement();
			st.execute(strSQL);
			logger.debug("SQL Query finished");
		} catch (SQLException e) {
		    e.printStackTrace();
		} finally {
		    if (conn != null) {
		        try { conn.close(); } catch (SQLException e) {}
		    }
		}
	}
	
	public void setSQLNumberFormat(Locale loc) {
		this.sqlNumberFormat = DecimalFormat.getNumberInstance(loc);
	}
	
	public void setInputNumberFormat(Locale loc) {
		this.inputNumberFormat = DecimalFormat.getNumberInstance(loc);
	}

	public String getFieldSeperator() {
		return fieldSeperator;
	}

	public void setFieldSeperator(String fieldSeperator) {
		this.fieldSeperator = fieldSeperator;
	}

	public String getJdbcSQLStatement() {
		return jdbcSQLStatement;
	}

	public void setJdbcSQLStatement(String jdbcSQLStatement) {
		this.jdbcSQLStatement = jdbcSQLStatement;
	}

	public String[] getFileAttributes() {
		return fileAttributes;
	}

	public void setFileAttributes(String[] fileAttributes) {
		this.fileAttributes = fileAttributes;
	}

	public JDBCDatasource getDataSource() {
		return dataSource;
	}

	public void setDataSource(JDBCDatasource dataSource) {
		this.dataSource = dataSource;
	}
}