package gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import java.awt.FlowLayout;
import javax.swing.JList;
import java.awt.Dimension;

import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import auth.InvalidCredentialsException;
import auth.User;
import auth.UserAuthentication;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DatabaseManager;
import main.Config;
import main.ContentScanner;
import main.EventLog;
import main.Main;

/**
 * Graphic interface to operate the security scanner
 * 
 * @author Nick Schillaci
 * @author Zackary Flake
 * @author Brandon Dixon
 */
public class ScannerGUI extends JFrame{

	static final String TITLE = "TigerScan";
	static final String TITLE_FULL = "TigerScan - Email Security Scanner";
	static final int FRAME_WIDTH = 500;
	static final int FRAME_HEIGHT = 400;
	static final URL ICON_URL = Main.class.getResource("/icon.png");
	static final URL SETTINGS_URL = Main.class.getResource("/settings.png");
	
	private ArrayList<String> filenames;
	private ContentScanner scanner;
	private int screenWidth;
	private int screenHeight;
	private DatabaseManager db;
	private String version;
	
	public ScannerGUI(ContentScanner scanner, DatabaseManager db, String version) {
		this.scanner = scanner;
		this.db = db;
		this.version = version;
		filenames = new ArrayList<String>();
		initializeUI();
	}
	
	private void initializeUI() {
		this.setTitle(TITLE);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				db.closeSQLConnection();
			}
		});
		ImageIcon icon = new ImageIcon(ICON_URL);
		this.setIconImage(icon.getImage());
		Toolkit tk = Toolkit.getDefaultToolkit();
		screenWidth = tk.getScreenSize().width;
		screenHeight = tk.getScreenSize().height;
		setLocation(screenWidth/3, screenHeight/3);
		setContentPane(mainPanel());
	}
	
	/**
	 * unused as of right now
	 */
	private JMenuBar mainMenuBar() {
		JMenuBar menu = new JMenuBar();
		//TODO add menu and items to menu bar
		return menu;
	}
	
	private JPanel mainPanel() {	
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(northPanel(), BorderLayout.NORTH);
		panel.add(southPanel(), BorderLayout.SOUTH);
		panel.add(centerPanel(), BorderLayout.CENTER);

		return panel;
	}
	
	private JPanel northPanel() {
		JPanel nPanel = new JPanel();
		//nPanel.setLayout(new BoxLayout(nPanel, BoxLayout.PAGE_AXIS));
		nPanel.setBorder(new EmptyBorder(10, 0, 10, 10));	
		JLabel nLabel = new JLabel(TITLE_FULL);
		nLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		nPanel.add(nLabel);
		return nPanel;
	}
	
	private JPanel centerPanel() {
		DefaultListModel listModel = new DefaultListModel();
		
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel fileListPanel = new JPanel();
		fileListPanel.setBorder(new EmptyBorder(00, 70, 20, 70));
		cPanel.add(fileListPanel, BorderLayout.CENTER);
		fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
		
		JList fileListBox = new JList();
		fileListBox.setFixedCellWidth(100);
		//fileListBox.setFixedCellHeight(25);
		fileListBox.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		fileListBox.setModel(listModel);
		fileListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane.add(fileListBox);
		scrollPane.setViewportView(fileListBox);
		fileListPanel.add(scrollPane);
		
		JPanel fileActionPanel = new JPanel();
		cPanel.add(fileActionPanel, BorderLayout.SOUTH);
		fileActionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
	
		JButton fileAddButton = new JButton("Add File");
		fileAddButton.setPreferredSize(new Dimension(125, 30));
		fileActionPanel.add(fileAddButton);
	
		fileAddButton.setMnemonic(KeyEvent.VK_A);
		fileAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				FileDialog fd = new FileDialog(new JFrame(), "Choose file to add", FileDialog.LOAD);
				fd.setVisible(true);
				if(fd.getFile() != null) {
					int response = 0;
					if(listModel.contains(fd.getFile())) {
						response = JOptionPane.showConfirmDialog(new JFrame(), "A file with this name has already been added to be scanned.\nDo you wish to add a duplicate of the file?", "Add Duplicate File", JOptionPane.YES_NO_OPTION);
					}
					if (response == JOptionPane.YES_OPTION) {
						filenames.add(fd.getDirectory() + fd.getFile()); // ArrayList filenames has the exact directory and file name
						listModel.addElement(/*fd.getDirectory() + */fd.getFile()); // currently displaying file name without directory
					}
				}
			}
		});
		
		JButton fileRemoveButton = new JButton("Remove File");
		fileRemoveButton.setPreferredSize(new Dimension(125, 30));
		fileActionPanel.add(fileRemoveButton);
		fileRemoveButton.setMnemonic(KeyEvent.VK_R);
		fileRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!fileListBox.isSelectionEmpty()) {
					filenames.remove(fileListBox.getSelectedIndex());
					listModel.remove(fileListBox.getSelectedIndex());
				}	
				else
					System.out.println("No file selected.");
			}
		});
		

		
		return cPanel;
	}
	
	private JPanel southPanel() {
		
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel fileScanPanel = new JPanel();
		sPanel.add(fileScanPanel, BorderLayout.NORTH);
		fileScanPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton fileScanButton = new JButton("Scan All");
		fileScanButton.setPreferredSize(new Dimension(125, 30));
		fileScanPanel.add(fileScanButton);
		fileScanButton.setMnemonic(KeyEvent.VK_S);
		fileScanButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if(filenames.size() == 0)
					System.out.println("No files to scan.");
				else {
					HashMap<String,Double> r = scanner.scanFiles(filenames);
					String sReport = "";
					int size = r.size();
					String[] fileNames = r.keySet().toArray(new String[0]);
					
					for (int i=0; i<size; i++) {
						double score = r.get(fileNames[i]);
						if(score > 50.0)
						sReport+=fileNames[i]+"\n - Email is confidential" +"\n";
						else
						sReport+=fileNames[i]+"\n - Email is not confidential" +"\n";
					}
					try {
						Config.emailScanned();
					} catch (IOException e) {
						System.err.println("Error accessing config file.");
					}

					
					JOptionPane.showMessageDialog(null,"Scanning complete:\n"+sReport);
				}
			}
		});
		
		
		JButton settingsButton = new JButton();
		ImageIcon icon = new ImageIcon(SETTINGS_URL);
		settingsButton.setIcon(icon);
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JDialog authDialog = new JDialog((JDialog) null, "Login", true);
				authDialog.setLayout(new GridLayout(2,1));
				ImageIcon icon = new ImageIcon(ICON_URL);
				authDialog.setIconImage(icon.getImage());
				
				JPanel authPanel = new JPanel();
				GridLayout authLayout = new GridLayout(4,1);
				authLayout.setVgap(2);
				authPanel.setLayout(authLayout);
				
				JLabel userLabel = new JLabel("Username");
				JTextField userText = new JTextField();
				JLabel passLabel = new JLabel("Password");
				JPasswordField passField = new JPasswordField();
				
				JButton loginButton = new JButton("Log In");
				loginButton.setMnemonic(KeyEvent.VK_L);
				loginButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ev){
						User user = null;
						String usernameString = userText.getText();
						try{
							user = UserAuthentication.login(usernameString, passField.getPassword());
						}
						catch(InvalidCredentialsException e1) //invalid username/password
						{
							JOptionPane.showMessageDialog(authDialog, "Invalid credentials", "Log-In Failed", JOptionPane.ERROR_MESSAGE);
						}
						catch(IOException e2) //users file not found
						{
							if (UserAuthentication.verifyDefaultAdmin(usernameString, passField.getPassword())) {
								JOptionPane.showMessageDialog(authDialog, "The default administrator information must be changed.\n"
										+ "Please enter new log-in credentials.", "Administrator", JOptionPane.WARNING_MESSAGE);
								
								user = new NewUserDialog(sPanel, ICON_URL).getUser();
							}
							else
								JOptionPane.showMessageDialog(authDialog, "Log-in information not found.\n"
										+ "Please contact your system administrator.", "Log-In Failed", JOptionPane.ERROR_MESSAGE);
						}
						if(user != null && user.isAdmin())
						{
							authDialog.dispose();
							createAdminDialog(db);
						}
					}
				});
				
				authPanel.add(userLabel);
				authPanel.add(userText);
				authPanel.add(passLabel);
				authPanel.add(passField);
				
				JPanel buttonPanel = new JPanel();
				buttonPanel.add(loginButton);
				buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
				
				authDialog.add(authPanel);
				authDialog.add(buttonPanel);
				authDialog.setSize(400, 200);
				authDialog.setLocationRelativeTo(sPanel);
				authDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				authDialog.setVisible(true);
				
				
			}
		});
		settingsButton.setPreferredSize(new Dimension(125, 30));
		fileScanPanel.add(settingsButton);
		
		JButton logButton = new JButton("Show Log");
		logButton.setPreferredSize(new Dimension(125, 30));
		fileScanPanel.add(logButton);
		
		logButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//TODO add window to display log. afterwards, we will encrypt the log when writing and decrypt it here
				JDialog logDialog = new JDialog((JDialog) null, "Log", true);
				logDialog.setSize(500, 500);
				try{
				File logFile = new File(EventLog.getLogFilename());
				FileReader fr = new FileReader(logFile);
				BufferedReader br = new BufferedReader(fr);
				
				String line = br.readLine();
				String log = "";
				while(line != null){
					log += "" + line + "\n";
					line = br.readLine();
				}
				br.close();

				JTextArea logArea = new JTextArea(25,40);
				logArea.setText(log);
				logArea.setEditable(false);
				
				JPanel logPanel = new JPanel();
				JScrollPane logPane = new JScrollPane(logArea);
				logPanel.add(logPane);
				
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event){
						logDialog.dispose();
					}
				});
				logPanel.add(closeButton);
				logDialog.add(logPanel);
				
				logDialog.setLocationRelativeTo(sPanel);
				logDialog.setVisible(true);	
				}
				catch(Exception excep)
				{
					JOptionPane.showMessageDialog(logDialog, "Event Log Not Found", "Unable to Show Log", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JLabel labelVersion = new JLabel("Version " + version);
		labelVersion.setForeground(Color.GRAY);
		labelVersion.setHorizontalAlignment(SwingConstants.CENTER);
		labelVersion.setFont(new Font("Tahoma", Font.PLAIN, 9));
		sPanel.add(labelVersion, BorderLayout.SOUTH);
		
		return sPanel;
	}
	
	// this won't be needed when we can add the scanner to the gui
	public ArrayList<String> getFilesToScan() {
		return filenames;
	}
	
	public void createAdminDialog(DatabaseManager db){
		AdminSettings admin = new AdminSettings(db);
	}	
	
}
