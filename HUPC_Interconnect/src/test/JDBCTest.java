package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.postgresql.ds.PGPoolingDataSource;

public class JDBCTest {
	PGPoolingDataSource source = null;
	
	void initConnectionPool() throws Exception {
		this.source = new PGPoolingDataSource();
		source.setDataSourceName("laborana");
		source.setServerName("192.168.0.100");
		source.setDatabaseName("laborana");
		source.setUser("postgres");
		source.setPassword("joe2cool");
		source.setMaxConnections(10);
		source.setInitialConnections(1);
	}
	
	void runInsertTest() throws Exception {
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://192.168.0.100/laborana";
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "joe2cool");
		//props.setProperty("ssl","true");
		Connection conn = DriverManager.getConnection(url, props);
		
		Statement st = conn.createStatement();
		st.execute("insert into timestamptest (\"Test\") values ('Java Test xxx');");
		
		st.close();
		conn.close();
	}
	
	void runTest() throws Exception {
		Connection conn = null;
		try {
		    conn = source.getConnection();
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM typen");
			while (rs.next()) {
			    System.out.print("Column 2 returned ");
			    System.out.println(rs.getString(3));
			}
		} catch (SQLException e) {
		    e.printStackTrace();
		} finally {
		    if (conn != null) {
		        try { conn.close(); } catch (SQLException e) {}
		    }
		}
	}
	
	public static void main(String[] args) {
		JDBCTest t = new JDBCTest();
		try {
			t.initConnectionPool();
			for(int i=0; i<20; i++) {
				t.runTest();
				Thread.sleep(5000);
				System.out.println(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
