
public class mainClass {	
	public static void main (String[] args) {
		for(int i = 0; i < args.length; i++) {
			System.out.println("args[" +i+ "]:\t " + args[i]);
		}
		
		if(checkflag(args, "-help")) {
			displayHelp();
			return;
		}
		
		String serialport = getString(args, "-port");
		if(serialport != null) {
			try {
				(new TwoWaySerialComm(null)).connect(serialport);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		if(checkflag(args, "-gui")) {
			new GUI();
			return;
		}
		
		if(checkflag(args, "-list")) {
			TwoWaySerialComm.listPorts(false);
			return;
		}
		
		if(checkflag(args, "-ilist")) {
			String port = TwoWaySerialComm.listPorts(true);
			System.out.println("Using port \"" +port+ "\"");

			try {
				(new TwoWaySerialComm(null)).connect(port);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}

		displayHelp();
		return;
	}
	
	private static void displayHelp() {
		System.err.println("Usage: java -jar serialcomm.jar <OPTION>");
		System.err.println();
		System.err.println("<OPTION> may be:");
		System.err.println("-help                Displays this help page");
		System.err.println("-port <PORT>         PORT is a serial port");
		System.err.println("-gui                 Starts in GUI-Mode");
		System.err.println("-list                prints a list of available ports");
		System.err.println("-ilist               same as -list but lets you choose one");
		System.err.println();
		System.err.println("Entering more than one Option results in undefined behavior.");
	}
	
	/**
	 * Says if the @flag is set in @args
	 * @param args The @args-Array to be evaluated
	 * @param flag Is the flag to be searched. @flag may start with leading dash.
	 * @return
	 */
	private static boolean checkflag(String[] args, String flag) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase(flag) || args[i].equalsIgnoreCase("-"+flag))
				return true;
		}
		return false;
	}

	private static Integer getInt(String[] args, String flag) {
		try {
			for(int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase(flag) || args[i].equalsIgnoreCase("-"+flag)) {
					return new Integer(Integer.parseInt(args[i+1]));
				}
			}
		} catch (NullPointerException e) {
			// DO NOTHING
		}
		return null;
	}

	private static String getString(String[] args, String flag) {
		try {
			for(int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase(flag) || args[i].equalsIgnoreCase("-"+flag)) {
					if(args[i+1].startsWith("-"))
						return null;
					return args[i+1];
				}
			}
		} catch (NullPointerException e) {
			// DO NOTHING
		}
		return null;
	}
}
