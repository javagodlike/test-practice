package mock.zjunit;

public class StaticMethod {

	public void test() {
		long startTime = System.currentTimeMillis();
		System.out.println(startTime);
		queryDb();
	}

	public Object queryDb() {
		System.out.println("queryDb");
		return null;
	}

	public static boolean checkPerson(String p) {
		return true;
	}

}
