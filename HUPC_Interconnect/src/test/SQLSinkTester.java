package test;

import org.apache.log4j.PropertyConfigurator;

import de.hupc.sinks.SQLSink;

public class SQLSinkTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("./ini/log4j.properties");
		String[] attr = {"Feld1", "Feld2", "Feld3"};
		SQLSink s = new SQLSink("./test/Test.csv", attr, "INSERT INTO TestTabelle (F1, F2, F3) values ('%Feld1%', %Feld2%, %Feld3%)");
		try {
			s.startProcessing();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
