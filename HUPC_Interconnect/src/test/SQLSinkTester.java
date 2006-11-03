package test;

import java.util.Locale;

import org.apache.log4j.PropertyConfigurator;

import de.hupc.sinks.SQLSink;

public class SQLSinkTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("./ini/log4j.properties");
		String[] attr = {"Feld1", "Feld2", "Feld3"};
		SQLSink s = new SQLSink(attr, "INSERT INTO TestTabelle (F1, F2, F3) values ('%Feld1%', %Feld2%, %Feld3%)");		
		Locale locale = new Locale("de");
		s.setInputNumberFormat(locale);
		
		try {
			s.startProcessing("./test/CSV_Input/Test.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
