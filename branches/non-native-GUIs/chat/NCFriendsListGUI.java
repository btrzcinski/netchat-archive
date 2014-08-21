package netchat.module.chat;

import netchat.util.*;
import netchat.module.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;

/**
 * The NCFriendsListGUI houses the friends list, sign off, and system tray capabilities of the default chat module.
 * @author Andy Street, 2007
 */

public class NCFriendsListGUI extends JFrame {

	private JPanel mainPanel = null;
	private JPanel titlePanel = null;
	private JPanel buttonPanel = null;
	private JPanel addFriendPanel = null;
	private JLabel addFriendStatusLabel = null;
	private JLabel addFriendLabel = null;
	private JButton friendButton = null;
	private JButton signOutButton = null;
	private JButton acceptAddFriendButton = null;
	private JScrollPane friendsListScrollPane = null;
	
	private Timer processingTimer = null;
	private JTextField addFriendTextField = null;
	private GridBagConstraints addFriendPanelConstraints = null;
	
	private ArrayList<JLabel> friendsListComponents = null;
	
	private ChangeableJList friendsList = null;
	private NCChatModule chatMod = null;
	
	private int frameWidth = 170;
	private int frameHeight = 400;
	private int addFriendSize = 50;
	private boolean addingFriend = false;
	
	private SystemTray tray = null;
	private TrayIcon trayIcon = null;
	private PopupMenu trayMenu = null;
	private MenuItem showItem = null;
	private boolean traySupported = false;
	
	//Friend Add Codes
	public static final int ERROR = 255;
	public static final int SUCCESS = 254;
	public static final int NOTIFY = 253;
	
	//Status Codes
	public static final int ONLINE = 255;
	public static final int OFFLINE = 254;
	public static final int AWAY = 253;
	public static final int BUSY = 252;
	
	public NCFriendsListGUI(NCChatModule cMod) {
		super();
		chatMod = cMod;
		initialize();
	}
	private void initialize() {
		this.setSize(frameWidth, frameHeight);
		this.setLocation(700, 290);
		this.setResizable(false);
		
		
		traySupported = SystemTray.isSupported();
		
		if(traySupported)
		{
			Debug.println("Initializing System Tray...", 1);
			tray = SystemTray.getSystemTray();
			
			trayMenu = new PopupMenu();
			
			showItem = new MenuItem("Show Netchat");
			showItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					showList();
				}
			});
			
			
			MenuItem signOffItem = new MenuItem("Sign Off");
			signOffItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					int confirmationCode = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to sign out?", "Sign out?",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(confirmationCode == JOptionPane.YES_OPTION)
						chatMod.signOut();
				}
			});
			
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					int confirmationCode = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to exit NetChat?", "Exit NetChat?",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(confirmationCode == JOptionPane.YES_OPTION)
						System.exit(0);
				}
			});
			
			trayMenu.add(showItem);		//Show NetChat
			trayMenu.addSeparator();	//------------------
			trayMenu.add(signOffItem);	//Sign Off
			trayMenu.add(exitItem);		//Exit
			
			trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icons/nc.png")).getImage(),
					"NetChat : JClient", trayMenu);
			trayIcon.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e)
				{
					if(e.getClickCount() == 2)
						showList();
				}
			});

			trayIcon.setImageAutoSize(true);

			this.setDefaultCloseOperation(HIDE_ON_CLOSE);
			
			try{
				tray.add(trayIcon);
			}
			catch(AWTException e)
			{
				Debug.println("Couldn't add tray icon to system tray...", 0);
				traySupported = false;
				this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}
		}
		else
		{
			Debug.println("System tray unsupported...", 0);
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e)
				{
					signOutButton.setEnabled(false);
					int confirmationCode = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to sign out?", "Sign out?",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(confirmationCode == JOptionPane.OK_OPTION)
						chatMod.signOut();
					else
						signOutButton.setEnabled(true);
				}
			});
		}
		
		processingTimer = new Timer(250, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String text;
				if((text = addFriendStatusLabel.getText()).length() == 16)
					addFriendStatusLabel.setText("Processing");
				else
					addFriendStatusLabel.setText("." + text + ".");
			}
		});
		
		this.setContentPane(getMainPanel());
		this.setIconImage(new ImageIcon(getClass().getResource("/icons/nc.png")).getImage());
		this.setTitle("NChat FL");
		this.setVisible(true);
	}
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			addFriendPanelConstraints = new GridBagConstraints();
			addFriendPanelConstraints.gridx = 0;
			addFriendPanelConstraints.fill = GridBagConstraints.BOTH;
			addFriendPanelConstraints.insets = new Insets(-2, 3, 3, 3);
			addFriendPanelConstraints.gridy = 3;
			
			GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
			buttonPanelConstraints.gridx = 0;
			buttonPanelConstraints.insets = new Insets(2, 4, 4, 4);
			buttonPanelConstraints.gridy = 2;
			
			GridBagConstraints titlePanelConstraints = new GridBagConstraints();
			titlePanelConstraints.gridx = 0;
			titlePanelConstraints.fill = GridBagConstraints.BOTH;
			titlePanelConstraints.insets = new Insets(4, 4, 0, 4);
			titlePanelConstraints.gridy = 0;
			
			GridBagConstraints friendsListConstraints = new GridBagConstraints();
			friendsListConstraints.fill = GridBagConstraints.BOTH;
			friendsListConstraints.gridx = 0;
			friendsListConstraints.gridy = 1;
			friendsListConstraints.weightx = 1.0;
			friendsListConstraints.insets = new Insets(0, 4, 0, 4);
			friendsListConstraints.weighty = 1.0;
			
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.setBackground(Color.darkGray);
			mainPanel.add(getFriendsListScrollPane(), friendsListConstraints);
			mainPanel.add(getButtonPanel(), buttonPanelConstraints);
			mainPanel.add(getTitlePanel(), titlePanelConstraints);
			getAddFriendPanel();
		}
		return mainPanel;
	}
	private JScrollPane getFriendsListScrollPane() {
		if (friendsListScrollPane == null) {
			friendsListScrollPane = new JScrollPane();
			friendsListScrollPane.setViewportView(getFriendsList());
		}
		return friendsListScrollPane;
	}
	private ChangeableJList getFriendsList() {
		if (friendsList == null) {
			friendsList = new ChangeableJList();
			friendsList.setBackground(new Color(171, 223, 255));
			friendsList.setSelectionBackground(new Color(90, 100, 255));
			friendsList.setCellRenderer(new ImageIconListCellRenderer());
			friendsList.addMouseListener(new DoubleClickListener());
			friendsList.addKeyListener(new FriendsListKeyListener());
			
			friendsListComponents = new ArrayList<JLabel>();
		}
		return friendsList;
	}
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			
			
			addFriendStatusLabel = new JLabel();
			addFriendStatusLabel.setText("");
			addFriendStatusLabel.setIcon(new ImageIcon(getClass().getResource("/icons/netchat_small.png")));
			
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			titlePanel.setBackground(Color.darkGray);
			titlePanel.add(addFriendStatusLabel, new GridBagConstraints());
		}
		return titlePanel;
	}
	public void addFriend(String friend, int statusCode)
	{
		Debug.println("Adding friend " + friend + "...", 2);
		
		addFriendToList(friend, statusCode);
		
		acceptAddFriendButton.setEnabled(true);
		friendButton.setEnabled(true);
		processingTimer.stop();
		setAddFriendStatus("Friend added.", SUCCESS);
	}
	public void addFriendToList(String friend, int statusCode)
	{	
		JLabel listComponent = new JLabel();
		listComponent.setText(friend);
		listComponent.setOpaque(true);
		listComponent.setBackground(getFriendsList().getBackground());
		listComponent.setForeground(getFriendsList().getForeground());
		
		switch(statusCode)
		{
			case ONLINE: listComponent.setIcon(new ImageIcon(getClass().getResource("/icons/nc.png"))); break;
			case OFFLINE: listComponent.setIcon(new ImageIcon(getClass().getResource("/icons/offline.png"))); break;
			case AWAY: break;
			case BUSY: break;
			default: Debug.println("Unknown friend status in setAddToList...", 0);
		}
		
		friendsListComponents.add(listComponent);
		friendsList.getVectorization().addElement(friend);
		friendsListScrollPane.validate();
	}
	public void updateFriendStatus(String friend, int statusCode)
	{
		JLabel listComponent = friendsListComponents.get(friendsList.getVectorization().indexOf(friend));
		switch(statusCode)
		{
			case ONLINE:	listComponent.setIcon(new ImageIcon(getClass().getResource("/icons/nc.png")));
					friendsList.repaint();
					if(traySupported)
						trayIcon.displayMessage("Friend Status", friend + " signed on.",
							TrayIcon.MessageType.INFO);
					else
						System.out.println("---> " + friend + " signed on.");
					break;
			
			case OFFLINE: 	listComponent.setIcon(new ImageIcon(getClass().getResource("/icons/offline.png")));
					friendsList.repaint();
					if(traySupported)
						trayIcon.displayMessage("Friend Status", friend + " signed off.",
							TrayIcon.MessageType.INFO);
					else
						System.out.println("---> " + friend + " signed off.");
					break;
			
			case AWAY:	if(traySupported)
						trayIcon.displayMessage("Friend Status", friend + " signed off.",
							TrayIcon.MessageType.INFO);
					else
						System.out.println("---> " + friend + " is away.");
					break;
			
			case BUSY:	break;
			
			default: Debug.println("Unknown friend status in setAddToList...", 0);
		}
	}
	public void friendDenied()
	{
		acceptAddFriendButton.setEnabled(true);
		friendButton.setEnabled(true);
		processingTimer.stop();
		setAddFriendStatus("Friend denied.", ERROR);
	}
	public void setAddFriendStatus(String message, int status)
	{
		switch(status)
		{
			case SUCCESS: addFriendStatusLabel.setForeground(Color.green); break;
			case ERROR: addFriendStatusLabel.setForeground(Color.red); break;
			case NOTIFY: addFriendStatusLabel.setForeground(Color.white); break;
			default: Debug.println("Unknown message type in setAddFriendStatus...", 0); return;
		}
		
		addFriendLabel.setText("");
		addFriendStatusLabel.setText(message);
	}
	public void setProcessing()
	{
		setAddFriendStatus("Processing", NOTIFY);
		
		processingTimer.start();
	}
	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				int location = friendsList.locationToIndex(e.getPoint());
				if(location > -1)
					chatMod.launchChatPane((String)(friendsList.getModel().getElementAt(location)));
			}
		}
	}
	private class FriendsListKeyListener extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				String question;
				int confirmationCode = 0;
				int[] indices = friendsList.getSelectedIndices();
				if(indices.length > 0)
				{
					if(indices.length == 1)
					{
						question = "Are you sure you want to remove " +
								friendsList.getModel().getElementAt(indices[0]);
						question += "\nfrom your friends list?"; 
					}
					else
					{
						question = "Are you sure you want to remove these people";
						question += "\nfrom your friends list?";
					}
					confirmationCode = JOptionPane.showConfirmDialog(null, question, "Remove Friend(s)?",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(confirmationCode == JOptionPane.OK_OPTION)
						for(int i = 0; i < indices.length; i++)
						{
							int loc = indices[i] - i;
							String friend = (String)(friendsList.getModel().getElementAt(loc));
							friendsList.getVectorization().remove(loc);
							friendsListComponents.remove(loc);
							chatMod.removeFriend(friend);
						}
				}
			}
		}
	}
	private class AddFriendKeyListener extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				acceptAddFriendButton.doClick();
			}
		}
	}
	private class ChangeableJList extends JList //This idea/implementation from Sun JList advanced tutorial
	{
		public ChangeableJList()
		{
			super(new DefaultListModel());
		}
		public DefaultListModel getVectorization()
		{
			return (DefaultListModel)(getModel());
		}
	}
	private class ImageIconListCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
		{	
			JLabel listComponent = friendsListComponents.get(index);
			
			if(isSelected)
			{
				listComponent.setBackground(list.getSelectionBackground());
				listComponent.setForeground(list.getSelectionForeground());
			}
			else
			{
				listComponent.setBackground(friendsList.getBackground());
				listComponent.setForeground(friendsList.getForeground());
			}
			
			return listComponent;
		}
	}
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(1);
			buttonPanel = new JPanel();
			buttonPanel.setBackground(Color.darkGray);
			buttonPanel.setLayout(gridLayout);
			buttonPanel.add(getFriendButton(), null);
			buttonPanel.add(getSignOutButton(), null);
		}
		return buttonPanel;
	}

	private JButton getFriendButton() {
		if (friendButton == null) {
			friendButton = new JButton();
			friendButton.setText("<html><center>Add<p>Friend</center></html>");
			friendButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					friendButton.setEnabled(false);
					
					if(addingFriend)
					{
						addingFriend = false;
						setResizable(true);
						setSize(frameWidth, frameHeight);
						setResizable(false);
						
						mainPanel.remove(addFriendPanel);
						
						setAddFriendStatus("", NOTIFY);
						addFriendLabel.setText("Add Friend:");
						
						addFriendTextField.setText("");
						
						friendsList.requestFocus();
					}
					else
					{
						addingFriend = true;
						setResizable(true);
						setSize(frameWidth, frameHeight + addFriendSize);
						setResizable(false);
						
						mainPanel.add(addFriendPanel, addFriendPanelConstraints);
						
						addFriendTextField.requestFocus();
					}
					
					friendButton.setEnabled(true);
				}
			});
		}
		return friendButton;
	}
	private JButton getSignOutButton() {
		if (signOutButton == null) {
			signOutButton = new JButton();
			signOutButton.setPreferredSize(new Dimension(76, 35));
			signOutButton.setText("<html><center>Sign<p>Out</center></html>");
			signOutButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					signOutButton.setEnabled(false);
					
					int confirmationCode = JOptionPane.showConfirmDialog(null, 
							"Are you sure you want to sign out?", "Sign out?",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(confirmationCode == JOptionPane.YES_OPTION)
						chatMod.signOut();
					else
						signOutButton.setEnabled(true);
				}
			});
		}
		return signOutButton;
	}
	private JPanel getAddFriendPanel() {
		if (addFriendPanel == null) {
			GridBagConstraints addFriendStatusLabelConstraints = new GridBagConstraints();
			addFriendStatusLabelConstraints.gridx = 0;
			addFriendStatusLabelConstraints.gridwidth = 2;
			addFriendStatusLabelConstraints.anchor = GridBagConstraints.CENTER;
			addFriendStatusLabelConstraints.insets = new Insets(1, 0, 0, 1);
			addFriendStatusLabelConstraints.gridy = 0;
			
			addFriendStatusLabel = new JLabel();
			addFriendStatusLabel.setText("");
			
			GridBagConstraints addFriendLabelConstraints = new GridBagConstraints();
			addFriendLabelConstraints.gridx = 0;
			addFriendLabelConstraints.anchor = GridBagConstraints.WEST;
			addFriendLabelConstraints.insets = new Insets(2, 1, 0, 0);
			addFriendLabelConstraints.gridy = 0;
			
			addFriendLabel = new JLabel();
			addFriendLabel.setText("Add Friend:");
			addFriendLabel.setForeground(Color.white);
			
			GridBagConstraints acceptAddFriendButtonConstraints = new GridBagConstraints();
			acceptAddFriendButtonConstraints.gridx = 1;
			acceptAddFriendButtonConstraints.insets = new Insets(0, 0, 1, 2);
			acceptAddFriendButtonConstraints.gridy = 1;
			
			GridBagConstraints addFriendTextFieldConstraints = new GridBagConstraints();
			addFriendTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
			addFriendTextFieldConstraints.gridx = 0;
			addFriendTextFieldConstraints.gridy = 1;
			addFriendTextFieldConstraints.insets = new Insets(1, 1, 0, 2);
			addFriendTextFieldConstraints.weightx = 1.0;
			
			addFriendPanel = new JPanel();
			addFriendPanel.setLayout(new GridBagLayout());
			addFriendPanel.setBackground(Color.darkGray);
			addFriendPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			addFriendPanel.add(getAddFriendTextField(), addFriendTextFieldConstraints);
			addFriendPanel.add(getAcceptAddFriendButton(), acceptAddFriendButtonConstraints);
			addFriendPanel.add(addFriendLabel, addFriendLabelConstraints);
			addFriendPanel.add(addFriendStatusLabel, addFriendStatusLabelConstraints);
		}
		return addFriendPanel;
	}
	private JTextField getAddFriendTextField() {
		if (addFriendTextField == null) {
			addFriendTextField = new JTextField();
			addFriendTextField.setText("");
			addFriendTextField.addKeyListener(new AddFriendKeyListener());
		}
		return addFriendTextField;
	}
	private JButton getAcceptAddFriendButton() {
		if (acceptAddFriendButton == null) {
			acceptAddFriendButton = new JButton();
			acceptAddFriendButton.setText("Add");
			acceptAddFriendButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					acceptAddFriendButton.setEnabled(false);
					String name = addFriendTextField.getText();
					if(!name.matches("^[a-zA-Z0-9]+$"))
					{
						setAddFriendStatus("Invalid username.", ERROR);
						acceptAddFriendButton.setEnabled(true);
						friendButton.setEnabled(true);
					}
					else
					{
						friendButton.setEnabled(false);
						setProcessing();
						chatMod.addFriend(name);
					}
				}
			});
		}
		return acceptAddFriendButton;
	}
	public void removeTrayIcon()
	{
		if(traySupported)
			tray.remove(trayIcon);
	}
	public void showList()
	{
		this.setVisible(true);
		this.toFront();
	}
}
