package org.drivux.client;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectoryCheck implements Runnable {
	private MainGUI gui;
	private List<String> justDeletedFromServer = new ArrayList<>();

	public DirectoryCheck(MainGUI gui) {
		this.gui = gui;
	}

	public void run() {
		try {
			while(gui.isMonitorTurnedOn()) {
				
				// Discover the state of both the local directory and remote one
				Map<String, String> remoteFiles = Synchronizer.getRemoteDirectoryFiles();
				Map<String, String> localFiles = Synchronizer.getLocalDirectoryFiles();
				
				// Discover what's been deleted in the local folder
				List<String> localDeletes = DeletionWatch.getDeleted();
				
				// Create a Set of all the unique file names across both the local
				// and remote folder.
				Set<String> allFileNames = new HashSet<>();
				allFileNames.addAll(remoteFiles.keySet());
				allFileNames.addAll(localFiles.keySet());
				
				// Look at each file name and its status on both the local
				// folder and/or remote. Decide what to do with the file.
				// The outcome of this block is a list of files to send, receive,
				// or delete.
				
				TransferUtility transferUtility = new TransferUtility();
				
				// Delete files from server that were deleted on client
				for (String toDelete : localDeletes) {
					if (!justDeletedFromServer.contains(toDelete)) {
						System.out.println(Synchronizer.sendDeleteRequest(toDelete, null, "DELETE"));
						gui.addLogMsg("Requesting server delete file \"" + toDelete + "\"");
					}
				}
				justDeletedFromServer.clear();
				allFileNames.removeAll(localDeletes);	// Don't try to process the deleted files again in this operation
				
				for (String fileName : allFileNames) {
					
					// If file in both systems:
					// 		If remote newer, download and overwrite
					//		If local newer, upload and overwrite
					//		If modify times equal, do nothing
					if (remoteFiles.containsKey(fileName) && localFiles.containsKey(fileName)) {
						String[] remoteMetadata = remoteFiles.get(fileName).split("\\*");
						int remoteSize = Integer.parseInt(remoteMetadata[0]);
						String remoteLastModified = remoteMetadata[1];
						
						String[] localMetadata = localFiles.get(fileName).split("\\*");
						int localSize = Integer.parseInt(localMetadata[0]);
						String localLastModified = localMetadata[1];
						
						if (remoteSize != localSize) {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

							Date remoteTime = format.parse(remoteLastModified);
							Date localTime = format.parse(localLastModified);
							
							int compareResult = remoteTime.compareTo(localTime);
							if (compareResult < 0) {	// Remote time earlier than local
								transferUtility.uploadFile(fileName);
								gui.addLogMsg("Overwrote server \"" + fileName + "\" with file from this PC.");
							} else if (compareResult > 0) {
								// Remote time after local
								// overwrite local
								transferUtility.downloadFile(fileName);
								gui.addLogMsg("Overwrote \"" + fileName + "\" with value from server.");
							}
						}
					}
					else if (!remoteFiles.containsKey(fileName) && localFiles.containsKey(fileName)) {
						// file only on client
						// file has either just been created or deleted off server
						String[] elements = localFiles.get(fileName).split("\\*");
						String size = elements[0];
						String status = Synchronizer.sendDeleteRequest(fileName, size, "CHECK_DELETED_STATUS");
						System.out.println(status);
						if ("DELETED_ON_SERVER".equals(status.trim())) {
							// delete it here
							gui.addLogMsg("Deleting local file \"" + fileName + "\". Deleted by user on different PC.");
							File file = new File(Settings.getLocalSyncDir() + fileName);
							file.delete();
							justDeletedFromServer.add(fileName);
						} else {
							// send file to server
							gui.addLogMsg("Uploading to server file \"" + fileName + "\"");
							transferUtility.uploadFile(fileName);
						}	
					}
					else if (remoteFiles.containsKey(fileName) && !localFiles.containsKey(fileName)
							&& !localDeletes.contains(fileName)) {
						// file only on server and not just deleted
						// download it
						transferUtility.downloadFile(fileName);
						gui.addLogMsg("Downloaded \"" + fileName + "\" from server.");
					}	
				}
				localDeletes.clear();
				
				// If file only in remote, and not in deleted list, download file
				// If file is in deleted list, delete from server
				// If file only here, push to server unless was deleted from server,
				
				// Pause for 5 seconds
				Thread.sleep(5000);
				//break;
			}
		} catch (InterruptedException|ParseException e) {
			gui.addLogMsg("ERROR: " + e);
		} 
	}
}