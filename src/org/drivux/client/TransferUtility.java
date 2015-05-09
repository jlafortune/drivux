package org.drivux.client;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Responsible for uploading and downloading files from the central server.
 * All file transfers are through SFTP.
 * @author john
 */
public class TransferUtility {
	
	public void uploadFile(String fileName) {
		Path path = Paths.get(Settings.getLocalSyncDir() + fileName);
		JSch jsch = new JSch();
	    Session session = null;
	    Channel channel = null;
	    ChannelSftp sftpChannel = null;
	    try {
	        session = jsch.getSession(Settings.getUsername(), "lafortu.net", 22);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(Settings.getPassword());
	        session.connect();
	
	        channel = session.openChannel("sftp");
	        channel.connect();
	        sftpChannel = (ChannelSftp) channel;

	        sftpChannel.put(path.toString(), Settings.getRemoteSyncDir() 
	        		+ path.getFileName().toString());
   
	    } catch (JSchException e) {
	        e.printStackTrace();  
	    } catch (SftpException e) {
	        e.printStackTrace();
	    } finally {
	    	sftpChannel.exit();
	    	session.disconnect();
	    	channel.disconnect();
	    }
	}
	
	public void downloadFile(String fileName) {
		Path path = Paths.get(Settings.getLocalSyncDir() + fileName);
		
		JSch jsch = new JSch();
	    Session session = null;
	    Channel channel = null;
	    ChannelSftp sftpChannel = null;
	    try {
	        session = jsch.getSession(Settings.getUsername(), "lafortu.net", 22);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(Settings.getPassword());
	        session.connect();
	
	        channel = session.openChannel("sftp");
	        channel.connect();
	        sftpChannel = (ChannelSftp) channel;
	        sftpChannel.get(Settings.getRemoteSyncDir() + path.getFileName().toString(), 
	        		path.toString());
  
	    } catch (JSchException e) {
	        e.printStackTrace();  
	    } catch (SftpException e) {
	        e.printStackTrace();
	    } finally {
	    	sftpChannel.exit();
	    	session.disconnect();
	    	channel.disconnect();
	    }
	}
}
