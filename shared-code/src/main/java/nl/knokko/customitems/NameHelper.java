package nl.knokko.customitems;

import java.util.Locale;

public class NameHelper {
	
	public static String getNiceEnumName(String name) {
		
		// The charAt(0) and substring(1) would be out of bounds for an empty string
		if (name.isEmpty())
			return name;
		return name.charAt(0) + name.substring(1).toLowerCase(Locale.ROOT).replaceAll("_", " ");
	}
	
	public static String versionName(int version) {
		// This dirty trick will work for now
		return "1." + version;
	}
	
	public static String getNiceEnumName(String name, int firstMcVersion, int lastMcVersion) {
		String niceName = getNiceEnumName(name);
		if (firstMcVersion == lastMcVersion) {
			return niceName + " (" + versionName(firstMcVersion) + ")";
		} else {
			if (firstMcVersion == MCVersions.FIRST_VERSION && lastMcVersion == MCVersions.LAST_VERSION) {
				return niceName;
			} else {
				return niceName + " (" + versionName(firstMcVersion) + " to " + versionName(lastMcVersion) + ")";
			}
		}
	}
}
