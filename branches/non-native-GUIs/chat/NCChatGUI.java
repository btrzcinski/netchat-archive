package netchat.module.chat;

import netchat.util.*;
import netchat.module.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

/**
 * The NCChatGUI is the GUI used to chat with people on your friends list.
 * @author Andy Street, 2007
 */

public class NCChatGUI extends JFrame {

	private JPanel mainPanel = null;
	private JLabel usernameLabel = null;
	private JScrollPane jScrollPane = null;
	private JTextPane conversationTextPane = null;
	private JTabbedPane textAndOptionsTabbedPane = null;
	private JLabel optionsPlaceHolder = null;
	private JLabel anotherPlaceholdingJLabel = null;
	private JPanel otherUsernamePanel = null;
	private JLabel otherUsernameTitle = null;
	private String username = null;
	private String otherUsername = null;
	private NCChatModule chatMod;
	private JLabel otherUsernameLabel = null;
	private JLabel adminPlaceholderLabel = null;
	private JScrollPane chatScrollPane = null;
	private JTextArea chatTextArea = null;
	private StyledDocument convoDoc = null;
	private MutableAttributeSet myUsernameHighlighting = null;
	private MutableAttributeSet otherUsernameHighlighting = null;
	private MutableAttributeSet warningMessageHighlighting = null;
	private boolean backlogged = false;
	
	private boolean firstMessage = true;
	private int frameX = 2;
	private int frameY = 2;
	private int width = 600;
	private int height = 320;
	
	//Message Codes
	public static final int BACKLOG = 253;
	public static final int WARNING = 254;
	public static final int CHAT = 255;
	
	public NCChatGUI(NCChatModule cm, String uname, String otherUname, int x, int y) {
		super();
		chatMod = cm;
		username = uname;
		otherUsername = otherUname;
		
		frameX = x;
		frameY = y;
		
		initialize();
	}

	private void initialize() {
		mainPanel = new JPanel();
		GridBagConstraints otherUsernamePanelConstraints = new GridBagConstraints();
		otherUsernamePanelConstraints.gridx = 0;
		otherUsernamePanelConstraints.gridy = 1;
		otherUsernamePanelConstraints.insets = new Insets(0, 5, 0, 5);
		
		GridBagConstraints textAndOptionsTabbedPaneConstraints = new GridBagConstraints();
		textAndOptionsTabbedPaneConstraints.fill = GridBagConstraints.BOTH;
		textAndOptionsTabbedPaneConstraints.gridy = 2;
		textAndOptionsTabbedPaneConstraints.weightx = 0.5D;
		textAndOptionsTabbedPaneConstraints.weighty = 0.5D;
		textAndOptionsTabbedPaneConstraints.gridheight = 1;
		textAndOptionsTabbedPaneConstraints.gridx = 1;
		
		GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
		scrollPaneConstraints.fill = GridBagConstraints.BOTH;
		scrollPaneConstraints.gridy = 1;
		scrollPaneConstraints.weightx = 0.5D;
		scrollPaneConstraints.weighty = 0.95D;
		scrollPaneConstraints.anchor = GridBagConstraints.CENTER;
		scrollPaneConstraints.gridheight = 1;
		scrollPaneConstraints.gridx = 1;
		
		GridBagConstraints usernameLabelConstraints = new GridBagConstraints();
		usernameLabelConstraints.gridx = 0;
		usernameLabelConstraints.gridheight = 1;
		usernameLabelConstraints.gridwidth = 1;
		usernameLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
		usernameLabelConstraints.weightx = 0.0D;
		usernameLabelConstraints.anchor = GridBagConstraints.CENTER;
		usernameLabelConstraints.ipadx = 40;
		usernameLabelConstraints.gridy = 2;
		
		usernameLabel = new JLabel();
		usernameLabel.setText(username);
		usernameLabel.setForeground(Color.white);
		usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.darkGray);
		mainPanel.setPreferredSize(new Dimension(400, 250));
		mainPanel.add(usernameLabel, usernameLabelConstraints);
		mainPanel.add(getJScrollPane(), scrollPaneConstraints);
		mainPanel.add(getTextAndOptionsTabbedPane(), textAndOptionsTabbedPaneConstraints);
		mainPanel.add(getOtherUsernamePanel(), otherUsernamePanelConstraints);
		
		setSize(width, height);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(frameX, frameY);
		setContentPane(mainPanel);
		setIconImage(new ImageIcon(getClass().getResource("/icons/nc.png")).getImage());
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				chatMod.removeChatPane(otherUsername);
				dispose();
			}
		});
		
		setTitle("NetChat - " + username + " : " + otherUsername);
		setVisible(true);
		setFocusable(true);
		
		chatTextArea.requestFocus();
	}
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setViewportView(getConversationTextPane());
			
		}
		return jScrollPane;
	}
	private JTextPane getConversationTextPane() {
		if (conversationTextPane == null) {
			conversationTextPane = new JTextPane();
			conversationTextPane.setEditable(false);
			conversationTextPane.setBackground(new Color(171, 223, 255));
			
			initConvoDoc();
		}
		return conversationTextPane;
	}
	private JTabbedPane getTextAndOptionsTabbedPane() {
		if (textAndOptionsTabbedPane == null) {
			adminPlaceholderLabel = new JLabel();
			adminPlaceholderLabel.setText("If you were an admin, perhaps you'd see things here?");
			anotherPlaceholdingJLabel = new JLabel();
			anotherPlaceholdingJLabel.setText("What exactly were you expecting?");
			
			optionsPlaceHolder = new JLabel();
			optionsPlaceHolder.setText("This is the future place for some options.  Yo.");
			
			textAndOptionsTabbedPane = new JTabbedPane(JTabbedPane.RIGHT, JTabbedPane.SCROLL_TAB_LAYOUT);
			textAndOptionsTabbedPane.setTabPlacement(JTabbedPane.RIGHT);
			textAndOptionsTabbedPane.addTab("Chat", null, getChatScrollPane(), null);
			textAndOptionsTabbedPane.addTab("Options", null, optionsPlaceHolder, null);
			textAndOptionsTabbedPane.addTab("Convos", null, anotherPlaceholdingJLabel, null);
			textAndOptionsTabbedPane.addTab("Admin", null, adminPlaceholderLabel, null);
		}
		return textAndOptionsTabbedPane;
	}
	private JPanel getOtherUsernamePanel() {
		if (otherUsernamePanel == null) {
			otherUsernameLabel = new JLabel();
			otherUsernameLabel.setText(otherUsername);
			otherUsernameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			otherUsernameLabel.setHorizontalAlignment(SwingConstants.LEADING);
			otherUsernameLabel.setForeground(Color.black);
			otherUsernameLabel.setName("otherUsernameLabel");
			
			otherUsernameTitle = new JLabel();
			otherUsernameTitle.setText("Chatting with:");
			otherUsernameTitle.setName("otherUsernameTitle");
			otherUsernameTitle.setForeground(Color.black);
			otherUsernameTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			
			otherUsernamePanel = new JPanel();
			otherUsernamePanel.setLayout(new BoxLayout(getOtherUsernamePanel(), BoxLayout.Y_AXIS));
			otherUsernamePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			otherUsernamePanel.setBackground(Color.orange);
			otherUsernamePanel.add(otherUsernameTitle, null);
			otherUsernamePanel.add(otherUsernameLabel, null);
		}
		return otherUsernamePanel;
	}
	private JScrollPane getChatScrollPane() {
		if (chatScrollPane == null) {
			chatScrollPane = new JScrollPane();
			chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			chatScrollPane.setViewportView(getMessageTextArea());
		}
		return chatScrollPane;
	}
	private JTextArea getMessageTextArea() {
		if (chatTextArea == null) {
			chatTextArea = new JTextArea();
			chatTextArea.setBackground(new Color(255, 205, 205));
			chatTextArea.setText("");
			chatTextArea.setLineWrap(true);
			
			ChatAction ca = new ChatAction("Enter Action");
			Object name = ca.getValue(Action.NAME);
			KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
			chatTextArea.getInputMap().put(enterKeyStroke, name);
			chatTextArea.getActionMap().put(name, ca);
		}
		return chatTextArea;
	}
	public void setTalkingWith(String s)
	{
		otherUsername = s;
		otherUsernameLabel.setText(s);
	}
	public void setMyName(String s)
	{
		username = s;
		usernameLabel.setText(s);
	}
	public void receiveMessage(String msg, int msgCode, String date)
	{
		addConvoText(msg, otherUsername, false, msgCode, date);
	}	
	public void initConvoDoc()
	{
		convoDoc = conversationTextPane.getStyledDocument();
		
		myUsernameHighlighting = new SimpleAttributeSet();
		StyleConstants.setForeground(myUsernameHighlighting, new Color(255, 0, 0));
		
		otherUsernameHighlighting = new SimpleAttributeSet();
		StyleConstants.setForeground(otherUsernameHighlighting, new Color(0, 0, 255));
		
		warningMessageHighlighting = new SimpleAttributeSet();
		StyleConstants.setForeground(warningMessageHighlighting, Color.gray);
		StyleConstants.setItalic(warningMessageHighlighting, true);
	}
	public void addConvoText(String msg, String uname, boolean isMe, int msgCode)
	{
		addConvoText(msg, uname, isMe, msgCode, null);
	}
	public void addConvoText(String msg, String uname, boolean isMe, int msgCode, String date) //date if different from now
	{
		if(firstMessage)
			firstMessage = false;
		else
		{
			if(msgCode == CHAT)
				uname = "\n" + uname;
			else if(msgCode == WARNING)
				msg = "\n" + msg;
		}
		
		try{	
			if(msgCode == CHAT)
			{
				
				String header = uname + " [" + chatMod.getTime() + "]";
				
				if(isMe)
					convoDoc.insertString(convoDoc.getLength(), header, myUsernameHighlighting);
				else
					convoDoc.insertString(convoDoc.getLength(), header, otherUsernameHighlighting);
				
				convoDoc.insertString(convoDoc.getLength(), ": " + msg.trim(), null);
			}
			else if(msgCode == WARNING)
				convoDoc.insertString(convoDoc.getLength(), msg, warningMessageHighlighting);
			else if(msgCode == BACKLOG)
			{
				if(!backlogged)
				{
					backlogged = true;
					
					String backlogText = "The following messages were sent to you while you were offline:";
					convoDoc.insertString(convoDoc.getLength(), backlogText, warningMessageHighlighting);
				}
				
				String header = "\n" + uname + " [" + date + "]";
				convoDoc.insertString(convoDoc.getLength(), header, otherUsernameHighlighting);
				
				convoDoc.insertString(convoDoc.getLength(), ": " + msg.trim(), null);
			}
			else
			{
				Debug.printError("Trying to insert text with invalid message code (" + msgCode + ")...");
				return;
			}
		}
		catch (BadLocationException e) {
			Debug.println("Tried to insert text into a bad location in the conversation document.", 0);
		}
		conversationTextPane.setCaretPosition(convoDoc.getLength());
	}
	private class ChatAction extends AbstractAction
	{
		public ChatAction(String actName)
		{
			putValue(Action.NAME, actName);
		}
		public void actionPerformed(ActionEvent e)
		{
			String msg = "";
			if(!((msg = chatTextArea.getText()).equals("")))
			{
				addConvoText(msg, username, true, CHAT);
				chatTextArea.setText("");
				chatMod.sendMessage(msg, otherUsername);
			}
		}
	}
}
