package org.drivux.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainGUI {
	private JTextArea logTextArea;
	private boolean monitor;
	protected MainGUI gui = this;

	public void showGUI() {
		//Create and set up the window.
        JFrame frame = new JFrame("Drivux");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);	// center on screen

        JPanel panel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        JButton btnSettings = new JButton("Settings");
        btnSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSettings.addActionListener(new SettingsFrame(gui));
        
        final JButton btnStartMonitor = new JButton("Start Monitoring");
        final JButton btnStopMonitor = new JButton("Stop Monitoring");
        btnStartMonitor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Specify what clicking "Start Monitoring" does
        btnStartMonitor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setMonitor(true);	// Mark monitoring as on
				btnStartMonitor.setEnabled(false);
				btnStopMonitor.setEnabled(true);
				
				// Spawn thread for period synchronization of directory
				Thread directoryCheckThread = new Thread(new DirectoryCheck(gui));
				directoryCheckThread.start();
				
				// Spawn thread to watch for file deletes from directory
				Path directoryPath = Paths.get(Settings.getLocalSyncDir());
				Thread deletionWatchThread;
				try {
					deletionWatchThread = new Thread(new DeletionWatch(directoryPath, false, gui));
					deletionWatchThread.start();
					addLogMsg("Monitoring of " + Settings.getLocalSyncDir() + " started!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
        	
        });
        
        btnStopMonitor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Specify what clicking "Stop Monitoring" does
        btnStopMonitor.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent event) {
				// Mark monitoring as off. Each thread checks monitor value before continuing
				// so this will effectively cause threads to end.
				btnStartMonitor.setEnabled(true);
				btnStopMonitor.setEnabled(false);
				setMonitor(false);
				addLogMsg("Monitoring of " + Settings.getLocalSyncDir() + " stopped!");
			}	
        });
        
        logTextArea = new JTextArea(15, 80);
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setText("Welcome to Drivux! If this is your first use, click Settings. "
        		+ "Otherwise, your previously saved settings have been loaded. As files are added, modified,"
        		+ " or deleted from your sync directory, log messages will show here so you can see what Drivux is doing.");
        
        JScrollPane areaScrollPane = new JScrollPane(logTextArea);
        areaScrollPane.setPreferredSize(new Dimension(400, 300));
        areaScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.add(btnSettings);
        buttonsPanel.add(btnStartMonitor);
        buttonsPanel.add(btnStopMonitor);
        panel.add(buttonsPanel);
        panel.add(areaScrollPane);
        
        frame.getContentPane().add(panel);

        //Display the window.
       frame.setVisible(true);
	}

	public void addLogMsg(String str) {
		if (logTextArea.getText().startsWith("Welcome")) {
			logTextArea.setText("");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String logMsg = sdf.format(new Date()) + " - " + str;
		
		logTextArea.setText(logTextArea.getText() + "\n" + logMsg);
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());	// Scroll to bottom
	}

	public boolean isMonitorTurnedOn() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
}
