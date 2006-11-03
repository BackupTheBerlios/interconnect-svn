package test;

public class RegExTester {

	public static void main(String[] args) {
		String str = ".4*";
		
		//System.out.println(str.matches("^[0-9|\\.\\,].*"));
		System.out.println(str.matches(".*\\p{Alpha}.*"));
	}

}
