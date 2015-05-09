package org.drivux.client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SettingsFrame extends JFrame implements ActionListener {
	private JPanel outerPanel;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JPasswordField txtPasswordConfirm;
	private JTextField txtSyncDir;
	private MainGUI gui;

	public SettingsFrame(MainGUI gui) {
		super("Drivux Settings");
		this.gui = gui;
		outerPanel = new JPanel();
		outerPanel.setLayout(new GridLayout(5,2));	

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new Save());
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() { 
					// User wishes to cancel
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
		});
		JLabel lblUsername = new JLabel("Username:");
		JLabel lblPassword = new JLabel("Password:");
		JLabel lblPasswordConfirm = new JLabel("Confirm Password:");
		JLabel lblSyncDir = new JLabel("Local Sync Directory:");
		

		txtUsername = new JTextField(40);
		txtUsername.setText(Settings.getUsername());
		txtPassword = new JPasswordField(40);
		txtPassword.setText(Settings.getPassword());
		txtPasswordConfirm = new JPasswordField(40);
		txtPasswordConfirm.setText(Settings.getPassword());
		txtSyncDir = new JTextField(40);
		txtSyncDir.setText(Settings.getLocalSyncDir());

		outerPanel.add(lblUsername);
		outerPanel.add(txtUsername);
		outerPanel.add(lblPassword);
		outerPanel.add(txtPassword);
		outerPanel.add(lblPasswordConfirm);
		outerPanel.add(txtPasswordConfirm);
		outerPanel.add(lblSyncDir);
		outerPanel.add(txtSyncDir);
		outerPanel.add(btnSave);
		outerPanel.add(btnCancel);
		this.getContentPane().add(outerPanel);
        this.setSize(new Dimension(400, 300));        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //this.pack();
        this.setLocationRelativeTo(null);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		new SettingsFrame(gui).setVisible(true);
	}
	
	/**
     * Updates contact info
     */
    private class Save implements ActionListener {

        /**
         * Updates contact info for contact
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
        	String password = (txtPassword.getPassword() == null) ? 
        			"" : String.valueOf(txtPassword.getPassword());
        	String passwordConfirm = (txtPasswordConfirm.getPassword() == null) ? 
        			"" : String.valueOf(txtPasswordConfirm.getPassword());
        	
        	if (txtUsername.getText().isEmpty() || password.isEmpty()
        			|| passwordConfirm.isEmpty() 
        			|| txtSyncDir.getText().isEmpty()) {
        		JOptionPane.showMessageDialog(new JFrame(),
                        "All fields are required.", "Drivux",
                        JOptionPane.WARNING_MESSAGE);
        	} else if (password.equals(passwordConfirm)) {
        		Settings.setUsername(txtUsername.getText());
        		Settings.setPassword(password);
        		String syncDir = txtSyncDir.getText();
        		if (!syncDir.endsWith(System.getProperty("file.separator"))) {
        			syncDir += System.getProperty("file.separator");
        		}
        		Settings.setLocalSyncDir(syncDir);
        		gui.addLogMsg("Settings saved successfully.");
                dispose();
            } else {
            	JOptionPane.showMessageDialog(new JFrame(),
                        "Passwords don't match. Try again.", "Drivux",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
