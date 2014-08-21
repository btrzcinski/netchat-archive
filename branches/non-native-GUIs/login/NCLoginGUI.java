package netchat.module.login;

import netchat.system.*;
import netchat.module.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Insets;
import java.awt.GridBagConstraints;

/**
 * The NCLoginGUI provides the interface for logining into the NetChat server.
 * @author Andy Street, 2007
 */

public class NCLoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel = null;
	private JPanel titlePanel = null;
	private JLabel titleLabel = null;
	private JTextField usernameTextField = null;
	private JLabel usernameLabel = null;
	private JPasswordField passwordField = null;
	private JLabel passwordLabel = null;
	private JButton loginButton = null;
	private JButton cancelButton = null;
	private JLabel statusLabel = null;
	private Color textForeground;
	private Timer processingTimer = null;
	private NCLoginModule mod = null;
	private String username = null;
	
	public NCLoginGUI(NCLoginModule l) {
		super();
		mod = l;
		initialize();
	}

	private void initialize() {
		mainPanel = new JPanel();
		textForeground = Color.white;
		
		GridBagConstraints statusLabelConstraints = new GridBagConstraints();
		statusLabelConstraints.gridx = 1;
		statusLabelConstraints.gridwidth = 4;
		statusLabelConstraints.insets = new Insets(90, 0, -15, 0);
		statusLabelConstraints.gridy = 0;
		statusLabel = new JLabel();
		statusLabel.setText("Welcome!");
		statusLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		statusLabel.setForeground(Color.white);
		
		GridBagConstraints cancelButtonConstraints = new GridBagConstraints();
		cancelButtonConstraints.gridx = 3;
		cancelButtonConstraints.gridwidth = 1;
		cancelButtonConstraints.insets = new Insets(15, 8, 10, 0);
		cancelButtonConstraints.gridy = 3;
		
		GridBagConstraints loginButtonConstraints = new GridBagConstraints();
		loginButtonConstraints.gridx = 1;
		loginButtonConstraints.gridwidth = 2;
		loginButtonConstraints.anchor = GridBagConstraints.WEST;
		loginButtonConstraints.ipadx = 5;
		loginButtonConstraints.insets = new Insets(15, 18, 10, 0);
		loginButtonConstraints.gridy = 3;
		
		GridBagConstraints passwordLabelConstraints = new GridBagConstraints();
		passwordLabelConstraints.gridx = 1;
		passwordLabelConstraints.insets = new Insets(0, 15, 0, 0);
		passwordLabelConstraints.gridy = 2;
		
		passwordLabel = new JLabel();
		passwordLabel.setText("Password:");
		passwordLabel.setForeground(textForeground);
		passwordLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		
		GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
		passwordFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
		passwordFieldConstraints.gridy = 2;
		passwordFieldConstraints.weightx = 1.0;
		passwordFieldConstraints.gridwidth = 3;
		passwordFieldConstraints.insets = new Insets(0, 5, 0, 15);
		passwordFieldConstraints.gridx = 2;
		
		GridBagConstraints usernameLabelConstraints = new GridBagConstraints();
		usernameLabelConstraints.gridx = 1;
		usernameLabelConstraints.gridwidth = 1;
		usernameLabelConstraints.insets = new Insets(0, 15, 2, 0);
		usernameLabelConstraints.weightx = 0.0D;
		usernameLabelConstraints.gridy = 1;
		
		usernameLabel = new JLabel();
		usernameLabel.setText("Username:");
		usernameLabel.setForeground(textForeground);
		usernameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		
		GridBagConstraints usernameTextFieldConstraints = new GridBagConstraints();
		usernameTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
		usernameTextFieldConstraints.gridy = 1;
		usernameTextFieldConstraints.weightx = 1.0;
		usernameTextFieldConstraints.gridwidth = 3;
		usernameTextFieldConstraints.insets = new Insets(0, 5, 2, 15);
		usernameTextFieldConstraints.gridx = 2;
		
		GridBagConstraints titlePanelConstraints = new GridBagConstraints();
		titlePanelConstraints.gridx = 1;
		titlePanelConstraints.gridwidth = 4;
		titlePanelConstraints.ipadx = 0;
		titlePanelConstraints.weightx = 0.0D;
		titlePanelConstraints.insets = new Insets(0, 0, 35, 0);
		titlePanelConstraints.gridy = 0;
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBounds(new Rectangle(0, 0, 250, 280));
		mainPanel.setBackground(Color.darkGray);
		mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		mainPanel.add(getTitlePanel(), titlePanelConstraints);
		mainPanel.add(getUsernameTextField(), usernameTextFieldConstraints);
		mainPanel.add(usernameLabel, usernameLabelConstraints);
		mainPanel.add(getPasswordField(), passwordFieldConstraints);
		mainPanel.add(passwordLabel, passwordLabelConstraints);
		mainPanel.add(getLoginButton(), loginButtonConstraints);
		mainPanel.add(getCancelButton(), cancelButtonConstraints);
		mainPanel.add(statusLabel, statusLabelConstraints);
		
		processingTimer = new Timer(250, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String text;
				if((text = statusLabel.getText()).length() == 16)
					statusLabel.setText("Processing");
				else
					statusLabel.setText("." + text + ".");
			}
		});
		
		this.setSize(200, 295);
		this.setTitle("NetChat Main Login");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(mainPanel);
		this.setIconImage(new ImageIcon(getClass().getResource("/icons/nc.png")).getImage());
		
		getRootPane().setDefaultButton(loginButton);
		
		this.setResizable(false);
		this.setLocation(450, 350);
		this.setVisible(true);
	}
	private JPanel getTitlePanel() {
		if (titlePanel == null) {			
			GridBagConstraints titleLabelConstraints = new GridBagConstraints();
			titleLabelConstraints.insets = new Insets(0, 5, 3, 5);
			titleLabelConstraints.gridy = 0;
			titleLabelConstraints.gridx = 0;
			
			titleLabel = new JLabel();
			titleLabel.setIcon(new ImageIcon(getClass().getResource("/icons/netchat_small.png")));
			
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			titlePanel.setBackground(Color.darkGray);
			titlePanel.add(titleLabel, titleLabelConstraints);
		}
		return titlePanel;
	}
	private JTextField getUsernameTextField() {
		if (usernameTextField == null) {
			usernameTextField = new JTextField();
			usernameTextField.setPreferredSize(new Dimension(125, 20));
			usernameTextField.setBackground(new Color(171, 223, 255));
		}
		return usernameTextField;
	}
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setPreferredSize(new Dimension(125, 20));
			passwordField.setBackground(new Color(171, 223, 255));
		}
		return passwordField;
	}
	private JButton getLoginButton() {
		if (loginButton == null) {
			loginButton = new JButton();
			loginButton.setText("Login!");
			loginButton.setFont(new Font("Dialog", Font.BOLD, 12));
			loginButton.setSelected(true);
			loginButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					setProcessing();
					username = usernameTextField.getText();
					mod.receiveCredentials(username, passwordField.getPassword());
				}
			});
		}
		return loginButton;
	}
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setFont(new Font("Dialog", Font.BOLD, 12));
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});
		}
		return cancelButton;
	}
	public void setError(String s)
	{
		statusLabel.setForeground(Color.red);
		statusLabel.setText(s);
	}
	public void setStatus(String s)
	{
		statusLabel.setForeground(textForeground);
		statusLabel.setText(s);
	}
	public void setProcessing()
	{
		setStatus("Processing");
		setEnabledAll(false);
		
		processingTimer.start();
	}
	public void acceptCredentials()
	{
		processingTimer.stop();
		this.dispose();
	}
	public void rejectCredentials(String reason)
	{
		processingTimer.stop();
		setError(reason);
		
		usernameTextField.setText("");
		passwordField.setText("");
		
		setEnabledAll(true);
	}
	public void setEnabledAll(boolean enab)
	{
		loginButton.setEnabled(enab);
		cancelButton.setEnabled(enab);
		usernameTextField.setEnabled(enab);
		passwordField.setEnabled(enab);
	}
	
	public void flash()
	{
		new Timer(20, new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
			mainPanel.setBackground(new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)));
		}}).start();
	}
}
