package org.drivux.client;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * User preferences including username, password, sync directory, etc.
 * 
 * Stored with the Java Preferences class which concerns itself with where
 * to store the values on the user's OS.
 * 
 * @author john
 */
public class Settings {
	private static Preferences prefs;
	private static List<String> invalidFileSuffixes = new ArrayList<>();
	private static List<String> invalidFilePrefixes = new ArrayList<>();
	
	static {
		// Build/load preferences
		prefs = Preferences.userNodeForPackage(Settings.class);
		
		// Register invalid file extensions that shouldn't be synced
		invalidFileSuffixes.add(".swp");
		invalidFileSuffixes.add(".swap");
		invalidFileSuffixes.add(".swx");
		invalidFileSuffixes.add(".tmp");
		invalidFileSuffixes.add(".temp");
		invalidFileSuffixes.add("~");	
		
		invalidFilePrefixes.add("~");
		invalidFilePrefixes.add(".");
	}
	
	public static String getUsername() {
		return prefs.get("USERNAME", "");
	}
	
	public static void setUsername(String username) {
		prefs.put("USERNAME", username);
	}
	
	public static String getPassword() {
		return prefs.get("PASSWORD", "");
	}
	
	public static void setPassword(String password) {
		prefs.put("PASSWORD", password);
	}
	
	public static String getLocalSyncDir() {
		return prefs.get("SYNC_DIR", "");
	}
	
	public static void setLocalSyncDir(String dir) {
		prefs.put("SYNC_DIR", dir);
	}
	
	public static String getRemoteSyncDir() {
		return "/home/drivux/" + getUsername() + "/";
	}
	
	public static List<String> getInvalidFileSuffixes() {
		return invalidFileSuffixes;
	}

	public static List<String> getInvalidFilePrefixes() {
		return invalidFilePrefixes;
	}
}
