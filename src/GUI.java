import javax.swing.*;

import gnu.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//test commit 3

public class GUI extends JFrame {
	private static final long serialVersionUID = -5114319476273773959L;

	private static final String version = "v 0.1";

	private static final int[] baudRate = {300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200};
	private static final int baudRate_default = 9600;

	private static final String logfile = "serialLog.log";

	public static final String[] logging = {"Debug", "Verbose", "Warning", "Error"};
	public static final String[] logging_tags  = {"DEBUG", "VRBSE", "WARNG", "ERROR", "SR IN", "SROUT"};
	public static final int LOG_DEBUG 		= 0;
	public static final int LOG_VERBOSE 	= 1;
	public static final int LOG_WARNING 	= 2;
	public static final int LOG_ERROR 		= 3;
	public static final int LOG_SERIAL_IN	= 4;
	public static final int LOG_SERIAL_OUT	= 5;
	public static final int logging_default = LOG_DEBUG;

	// Menu Bar
	private JMenuBar menubar;
	private JMenu[] menu;
	private JRadioButtonMenuItem[] baudRateMenuItems;
	private JRadioButtonMenuItem[] loggingMenuItems;
	private JCheckBoxMenuItem logSerialData;
	private ButtonGroup baudRateGroup = new ButtonGroup();
	private ButtonGroup portGroup = new ButtonGroup();
	private ButtonGroup loggingGroup = new ButtonGroup();

	// GUI
	private Container pane;
	private JTextArea textIn;
	private JTextField textOut;
	private JPanel ioPanel;
	private LayoutManager ioLayout;
	private JPanel buttonPanel;
	private LayoutManager buttonLayout;
	private JButton exitButton, reconnectButton, refreshButton;

	// Status-Bar
	private JLabel statusBar;

	public GUI() {
		//Oberflaeche bauen
		printText("Starting SERIAL-GUI by haegelix in Version \"" + version+ "\"", LOG_VERBOSE, 0, false);
		build();

		//Test-Outputs
		listPorts();		

		Dimension d = this.getMinimumSize();
		printText("Min-Width:  " +d.getWidth(), LOG_DEBUG, getLogLevel(), getLogSerial());
		printText("Min-Height: " +d.getHeight(), LOG_DEBUG, getLogLevel(), getLogSerial());

		Dimension e = this.getMaximumSize();
		printText("Max-Width:  " +e.getWidth(), LOG_DEBUG, getLogLevel(), getLogSerial());
		printText("Max-Height: " +e.getHeight(), LOG_DEBUG, getLogLevel(), getLogSerial());
	}

	private void build() {
		initMenuBar();
		initGUI();
		initStatusBar();
		initWindow();
	}

	private void initStatusBar() {
		statusBar = new StatusBar();
		GUI.printText("Created StatusBar", LOG_DEBUG, getLogLevel(), true);
		//getContentPane().add(statusBar);
	}

	private void initMenuBar() {		
		menubar = new JMenuBar();
		menu = new JMenu[3];
		// Menü-Punkt Bit-Rate
		menu[0] = new JMenu("Bit-Rate");
		baudRateMenuItems = new JRadioButtonMenuItem[baudRate.length];
		for(int i = 0; i < baudRate.length; i++) {
			baudRateMenuItems[i] = new JRadioButtonMenuItem("" + baudRate[i]);
			baudRateGroup.add(baudRateMenuItems[i]);
			menu[0].add(baudRateMenuItems[i]);
			if(baudRate[i] == baudRate_default)
				baudRateMenuItems[i].setSelected(true);
		}

		// Menü-Punkt Port
		menu[1] = new JMenu("Port");
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements() ) 
		{
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if(portIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL)
				continue;
			JRadioButtonMenuItem b = new JRadioButtonMenuItem(portIdentifier.getName());
			portGroup.add(b);
			menu[1].add(b);
			if(!portEnum.hasMoreElements())
				b.setSelected(true);
		}   

		// Menü-Punkt Logging
		menu[2] = new JMenu("Logging");
		loggingMenuItems = new JRadioButtonMenuItem[logging.length];
		for(int i = 0; i < logging.length; i++) {
			loggingMenuItems[i] = new JRadioButtonMenuItem(logging[i]);
			loggingGroup.add(loggingMenuItems[i]);
			menu[2].add(loggingMenuItems[i]);
			if(i == logging_default)
				loggingMenuItems[i].setSelected(true);
		}
		menu[2].addSeparator();
		logSerialData = new JCheckBoxMenuItem("Log Serial Data?");
		menu[2].add(logSerialData);

		// Aufbau der Menü-Bar
		for (int i = 0; i < menu.length; i++) {
			menubar.add(menu[i]);
		}
	}

	private void initGUI() {

		//Build Text-IO-Panel
		ioPanel = new JPanel();		
		ioLayout = new BoxLayout(ioPanel, BoxLayout.Y_AXIS);
		ioPanel.setLayout(ioLayout);
		ioPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		textIn = new JTextArea();
		textIn.setText("Choose port to connect to!");
		textIn.setEditable(false);
		textIn.setAlignmentX(LEFT_ALIGNMENT);
		ioPanel.add(textIn);

		textOut = new JTextField();
		textOut.setEditable(true);
		textOut.setAlignmentX(LEFT_ALIGNMENT);
		textOut.setToolTipText("Type msg here!");
		textOut.setMaximumSize(new Dimension((int) textOut.getMaximumSize().getWidth(), (int) textOut.getMinimumSize().getHeight()));
		textOut.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					//send Data
					printText("Sending Data now!", LOG_DEBUG, getLogLevel(), getLogSerial());
				}
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		ioPanel.add(textOut);


		//Build Button Panel
		buttonPanel = new JPanel();		
		buttonLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(buttonLayout);

		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		buttonPanel.add(exitButton);

		//TODO Implement me
		reconnectButton = new JButton("Reconnect");
		buttonPanel.add(reconnectButton);

		refreshButton = new JButton("Refresh GUI");
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Container pane = getContentPane();
				pane.removeAll();
				build();
			}
		});
		buttonPanel.add(refreshButton);

		//Build Great Panel (whole window)
		pane = getContentPane();
		pane.add(ioPanel, BorderLayout.CENTER);
		pane.add(buttonPanel, BorderLayout.PAGE_END);
	}

	private void initWindow() {
		this.setTitle("Test");
		this.setResizable(true);
		//this.setLocation(0, 0);
		this.setSize(400, 300);
		this.setAlwaysOnTop(true);
		this.setJMenuBar(menubar);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void printText(String s, int msgLevel, int logLevel, boolean logSerial) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		s = dateFormat.format(new Date()) + " [" + logging_tags[msgLevel] + "]: " + s;
		if(msgLevel >= logLevel) {
			try {
				if((msgLevel == LOG_SERIAL_IN || msgLevel == LOG_SERIAL_OUT) && !logSerial)
					throw new Exception();
				PrintWriter o = new PrintWriter(new BufferedWriter(new FileWriter(logfile, true)));
				o.println(s);
				o.close();
			} catch (FileNotFoundException e) {
				fetchLogfile();
			} catch (IOException e) {
				//TODO Display information or user
			} catch (NullPointerException e) {
				//do nothing
			} catch (Exception e) {
				//do nothing
			} 
		}
		System.out.println(s);
	}

	private static void fetchLogfile() {
		try {
			InputStream in = new FileInputStream(logfile);
			in.close();
		} catch (FileNotFoundException e) {
			File f = new File(logfile);
			try {
				f.createNewFile();
				printText("Created new logfile", LOG_WARNING, LOG_WARNING, true);
			} catch (IOException e1) {
				//TODO Display information to user
			}
		} catch (IOException e) {
			//TODO Display information to user
		}
	}

	/**
	 * Gets the index of the selected LogLevel
	 * @return index of the selected LogLevel
	 */
	public int getLogLevel() {
		for(int i = 0; i < loggingMenuItems.length; i++) {
			if(loggingMenuItems[i].isSelected()) 
				return i;
		}
		return -1;
	}

	/**
	 * Indicates if SerialData should be logged.
	 * @return True if yes, false else
	 */
	private boolean getLogSerial() {
		return logSerialData.isSelected();
	}

	/**
	 * Prints a list of Serial ports that can be connected to.
	 */
	private void listPorts(){
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements() ){
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			printText(portIdentifier.getName()  +  " - " +  TwoWaySerialComm.getPortTypeName(portIdentifier.getPortType()), LOG_DEBUG, getLogLevel(), logSerialData.isSelected());
		}
	}

	public JTextArea getTextIn() {
		return textIn;
	}

	public JTextField getTextOut() {
		return textOut;
	}
	
	//public 
}

class StatusBar extends JLabel {
	private static final long serialVersionUID = -9132208389016840806L;

	/** Creates a new instance of StatusBar */
	
	public StatusBar() {
		super();
		super.setPreferredSize(new Dimension(100, 16));
		setMessage("Ready");
	}

	public void setMessage(String message) {
		setText(" " + message);        
	} 
}
