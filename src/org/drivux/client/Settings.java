package org.drivux.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
	private static Properties props = new Properties();
	
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
		
		try {
			FileInputStream input = new FileInputStream("drivux.properties");
			props.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public static String getServerHostname() {
		return props.getProperty("serverHostname");
	}
	
	public static String getServerSyncScriptsDir() {
		return props.getProperty("serverSyncScriptsDirectory");
	}
}
