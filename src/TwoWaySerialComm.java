import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

//import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TwoWaySerialComm
{
	int mode;
	static final int MODE_GUI 		= 0;
	static final int MODE_CONSOLE 	= 1;
	
	GUI gui;
	JTextArea textIn;
	JTextField textOut;
	Thread sr;
	Thread sw;
	
    public TwoWaySerialComm(GUI gui)
    {
    	super();
    	
    	if(gui == null)
    		mode = MODE_CONSOLE;
    	else {
    		mode = MODE_GUI;
    		this.gui = gui;
    		textIn = gui.getTextIn();
    		textOut = gui.getTextOut();
    	}
    }
    
    public void restart(String portName) {
    	if(sr != null) {
    		sr.interrupt();
    		while(sr.isAlive());
    	}
    	if(sw != null) {
    		sw.interrupt();
    		while(sw.isAlive());
    	}
    	try {
			connect(portName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    void connect (String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            print("Port is currently in use!", GUI.LOG_ERROR);
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                print("Only serial ports are handled by this App.", GUI.LOG_ERROR);
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable {
        InputStream in;
        
        public SerialReader ( InputStream in ) {
            this.in = in;
        }
        
        public void run () {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ( ( len = this.in.read(buffer)) > -1 ) {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;
        
        public SerialWriter ( OutputStream out ) {
            this.out = out;
        }
        
        public void run () {
            try {                
                int c = 0;
                while ((c = System.in.read()) > -1){
                    this.out.write(c);
                }                
            }
            catch (IOException e) {
                e.printStackTrace();
            }            
        }
    }
    
    public static void main (String[] args) {
        try {
            (new TwoWaySerialComm(null)).connect("COM4");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void print(String s, int loglevel) {
    	
    }
}