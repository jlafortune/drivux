package org.drivux.client;

import javax.swing.UIManager;

/**
 * Main class that starts the program and shows the user interface.
 * @author john
 */
public class DrivuxClient {
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			// Schedule a job for the event-dispatching thread:
			// creating and showing this application's GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					MainGUI gui = new MainGUI();
					gui.showGUI();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
