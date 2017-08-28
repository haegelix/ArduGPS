import javax.swing.*;

import gnu.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//test commit 3

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final String version = "v 0.1";
	
	static final int[] baudRate = {300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200};
	static final int baudRate_default = 9600;
	
	static final String logfile = "serialLog.log";
	
	static final String[] logging = {"Debug", "Verbose", "Warning", "Error"};
	static final String[] logging_tags  = {"DEBUG", "VRBSE", "WARNG", "ERROR", "SR IN", "SROUT"};
	static final int LOG_DEBUG 		= 0;
	static final int LOG_VERBOSE 	= 1;
	static final int LOG_WARNING 	= 2;
	static final int LOG_ERROR 		= 3;
	static final int LOG_SERIAL_IN	= 4;
	static final int LOG_SERIAL_OUT	= 5;
	static final int logging_default = LOG_DEBUG;
	int loglevel = LOG_DEBUG;
	
	
	JMenuBar menubar;
	JMenu[] menu;
	JRadioButtonMenuItem[] baudRateMenuItems;
	JRadioButtonMenuItem[] loggingMenuItems;
	JCheckBoxMenuItem logSerialData;
	ButtonGroup baudRateGroup = new ButtonGroup();
	ButtonGroup portGroup = new ButtonGroup();
	ButtonGroup loggingGroup = new ButtonGroup();

	public GUI() {
		//Oberflaeche bauen
		print("Starting SERIAL-GUI by haegelix in Version \"" + version+ "\"", LOG_VERBOSE);
		build();
				
		//Test-Outputs
		listPorts();		

		Dimension d = this.getMinimumSize();
		print("Min-Width:  " +d.getWidth(), LOG_DEBUG);
		print("Min-Height: " +d.getHeight(), LOG_DEBUG);
		
		Dimension e = this.getMaximumSize();
		print("Max-Width:  " +e.getWidth(), LOG_DEBUG);
		print("Max-Height: " +e.getHeight(), LOG_DEBUG);
	}
	
	void build() {
		initMenuBar();
		initGUI();
		initWindow();
	}
	
	void initMenuBar() {		
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
	
	void initGUI() {
		Container pane;
		JTextArea textIn;
		JTextField textOut;
		JPanel ioPanel;
		LayoutManager ioLayout;
		JPanel buttonPanel;
		LayoutManager buttonLayout;
		JButton exitButton, reconnectButton, refreshButton;
		
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
	
	void initWindow() {
		this.setTitle("Test");
		this.setResizable(true);
		//this.setLocation(0, 0);
		this.setSize(400, 300);
		this.setAlwaysOnTop(true);
		this.setJMenuBar(menubar);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	void print(String s, int loglevel) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		s = dateFormat.format(new Date()) + " [" + logging_tags[loglevel] + "]: " + s;
		if(loglevel >= this.loglevel) {
			try {
				if((loglevel == LOG_SERIAL_IN || loglevel == LOG_SERIAL_OUT) && !logSerialData.isSelected())
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
	
	void fetchLogfile() {
		try {
			InputStream in = new FileInputStream(logfile);
			in.close();
		} catch (FileNotFoundException e) {
			File f = new File(logfile);
			try {
				f.createNewFile();
				print("Created new logfile", LOG_WARNING);
			} catch (IOException e1) {
				//TODO Display information to user
			}
		} catch (IOException e) {
			//TODO Display information to user
		}
	}
	
    void listPorts(){
        @SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ){
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            print(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()), LOG_DEBUG);
        }        
    }
    
    static String getPortTypeName(int portType){
        switch(portType){
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
}
