package nl.knokko.customitems;

public class MCVersions {
	
	public static final int VERSION1_12 = 12;
	public static final int VERSION1_13 = 13;
	public static final int VERSION1_14 = 14;
	public static final int VERSION1_15 = 15;
	public static final int VERSION1_16 = 16;
	public static final int VERSION1_17 = 17;
	public static final int VERSION1_18 = 18;
	public static final int VERSION1_19 = 19;
	public static final int VERSION1_20 = 20;
	public static final int VERSION1_21 = 21;
	
	public static final int FIRST_VERSION = VERSION1_12;
	public static final int LAST_VERSION = VERSION1_21;

	public static String createString(int version) {
		// This function might become more complicated in the future
		return "1." + version;
	}
}
