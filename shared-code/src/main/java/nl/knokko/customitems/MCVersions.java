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
		switch (version) {
			case 12: return "1.12.2";
			case 13: return "1.13.2";
			case 14: return "1.14.4";
			case 15: return "1.15.2";
			case 16: return "1.16.5";
			case 17: return "1.17.1";
			case 18: return "1.18.2";
			case 19: return "1.19.4";
			case 20: return "1.20.6";
			case 21: return "1.21.10";
		}
		// This function might become more complicated in the future
		return "1." + version;
	}
}
