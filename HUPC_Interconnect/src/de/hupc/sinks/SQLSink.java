package de.hupc.sinks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.log4j.Logger;

public class SQLSink {
	static Logger logger = Logger.getLogger("de.hupc");
	private String[] fileAttributes = null;	
	private String fileSourcePath = null;
	private String jdbcSQLStatement = null;
	private String fieldSeperator = ";";
	private NumberFormat inputNumberFormat = DecimalFormat.getNumberInstance(Locale.GERMANY);
	private NumberFormat sqlNumberFormat = DecimalFormat.getNumberInstance(Locale.US);
	
	public SQLSink(String pFileSourcePath, String[] pFileAttributes, String pJDBCSQLStatement) {
		this.fileAttributes = pFileAttributes;
		this.fileSourcePath = pFileSourcePath;
		this.jdbcSQLStatement = pJDBCSQLStatement;
	}
	
	public void startProcessing() throws Exception {
		this.processSourceFile();
	}
	
	/**
	 * Reads source file line by line and inserts data into database.
	 * @throws Exception
	 */
	private void processSourceFile() throws Exception {
		// read source file line by line
		BufferedReader bf = new BufferedReader(new FileReader(this.fileSourcePath));
		String line = null;
		while((line = bf.readLine()) != null) {
			String strSQL = this.generateSQLStatement(line);
			logger.debug("SQL String: " + strSQL);
		}
	}
	
	/**
	 * Generates the SQL statement by substituting placeholders in SQL template with attributes from input line.
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private String generateSQLStatement(String input) throws Exception {
		String strSQL = this.jdbcSQLStatement;
		
		String[] fields = input.split(this.fieldSeperator);
		if(fields.length != this.fileAttributes.length) {
			throw(new Exception("Input line \"" + input + "\" does not match expected number of attributes (" + this.fileAttributes.length + ")"));
		}
		for(int i=0; i<fields.length; i++) {
			String field = fields[i].trim();
			logger.debug("[" + this.fileAttributes[i] + "]: Original value: " + field);
			// try to format field if it's a number
			try {
				Number num = inputNumberFormat.parse(field);
				if(num != null) {						
					field = sqlNumberFormat.format(num);
					logger.debug("[" + this.fileAttributes[i] + "]: Looks like a number. After conversion: " + field);
				}
			} catch(ParseException pe) {}
			
			//System.out.println(this.fileAttributes[i] + ": " + field);
			strSQL = strSQL.replaceAll("%" + this.fileAttributes[i] + "%", field);
		}
		
		return strSQL;
	}
	
	/**
	 * Sends given SQL statement to database via JDBC. 
	 * @param strSQL
	 * @throws Exception
	 */
	private void sendSQL(String strSQL) throws Exception {
		
	}
	
	public void setLocale(Locale loc) {
		this.sqlNumberFormat = DecimalFormat.getNumberInstance(Locale.GERMANY);
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
}