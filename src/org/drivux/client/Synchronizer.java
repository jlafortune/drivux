package org.drivux.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Various operations to help with the synchronization of files between the
 * user's computer and the server. All synchronization occurs over HTTPS.
 */
public class Synchronizer {

	/** 
	 * Returns the contents of the user's directory on the central server.
	 * Response includes file name, size, and last modification time.
	 */
	public static Map<String, String> getRemoteDirectoryFiles() {
		Map<String, String> filesMap = new HashMap<>();
		String parameters;
		URL url;
		HttpsURLConnection connection = null;
		String response = "";
		try {
			parameters = "user="
					+ URLEncoder.encode(Settings.getUsername(), "UTF-8")
					+ "&pass="
					+ URLEncoder.encode(Settings.getPassword(), "UTF-8");

			// Create connection
			url = new URL("https://lafortu.net/drivux/checkDir.php");
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(parameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer responseSb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				responseSb.append(line);
				responseSb.append('\r');
			}
			rd.close();
			response = responseSb.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		response = response.trim();
		if (!response.isEmpty()) {
			String[] separateFiles = response.split(";");
			for (String str : separateFiles) {
				String[] metadata = str.split("\\|");
				String name = metadata[0];
				// Only include files that should be synced. No .swp files, for example.
				if (isNameValidForSync(name)) {
					filesMap.put(metadata[0], metadata[1]);
				}
			}
		}
		return filesMap;
	}

	/**
	 * Returns a Map containing the file name as the key and a concatenation
	 * of the file size in bytes with the file's last modification time as
	 * the value.
	 * @return
	 */
	public static Map<String, String> getLocalDirectoryFiles() {
		Map<String, String> filesMap = new HashMap<>();
		
		File dir = new File(Settings.getLocalSyncDir());
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.isDirectory() && isNameValidForSync(file.getName())) {
				long lastModified = file.lastModified();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				filesMap.put(file.getName(), file.length() + "*" + sdf.format(lastModified));
			}
		}
		
		return filesMap;
	}
	
	 /**
     * Returns whether this fileName should be synced to, or downloaded from,
     * the central server. There are some temporary and swap files that we do not want
     * to have synced.
     */
	private static boolean isNameValidForSync(String fileName) {
		List<String> invalidPrefixes = Settings.getInvalidFilePrefixes();
		List<String> invalidSuffixes = Settings.getInvalidFileSuffixes();
		
		for (String invalidPrefix : invalidPrefixes) {
			if (fileName.startsWith(invalidPrefix)) {
				return false;
			}
		}
		
		for (String invalidSuffix : invalidSuffixes) {
			if (fileName.endsWith(invalidSuffix)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Sends one of two requests to the central server:
	 * 	1. A request to delete a file
	 *  2. A request to check whether a file has been deleted on the server
	 *  
	 * The purpose of (2) is to resolve the scenario where a file is on the
	 * user's computer but not on the server. The user's PC needs to know
	 * whether to send the file to the server or delete the file on the local
	 * machine. If the file has been deleted on the server it should be deleted here.
	 * @param string 
	 */
	public static String sendDeleteRequest(String file, String size, String requestType) {
		String parameters;
		URL url;
		HttpsURLConnection connection = null;
		if (size == null) {
			size = "";
		}
		String response = "";
		try {
			parameters = "user="
					+ URLEncoder.encode(Settings.getUsername(), "UTF-8")
					+ "&pass="
					+ URLEncoder.encode(Settings.getPassword(), "UTF-8")
					+ "&file="
					+ URLEncoder.encode(file, "UTF-8")
					+ "&size="
					+ URLEncoder.encode(size, "UTF-8")
					+ "&operation=" + requestType;

			// Create connection
			url = new URL("https://lafortu.net/drivux/delete.php");
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(parameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer responseSb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				responseSb.append(line);
				responseSb.append('\r');
			}
			rd.close();
			response = responseSb.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return response;
	}
}
