package geekconprinter;
import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.willwinder.universalgcodesender.SerialConnection;

public class SerialCommunicator {
	private SerialConnection connection;
	private String commandTerminator = "\n";
    private String portName = "/dev/cu.usbmodem1421";
    private int baudRate = 115200;
	
    /**
     * Opens the connection to the printer.
     * portName is hardcoded to work with arduino on a mac
     * baudrate is set according to what the custom firmware is set to. 
     */
	public void openConnection()
	{	
		this.connection = new SerialConnection();
		
        try {
			connection.openPort(portName, baudRate);
		} catch (Exception e) {
			System.out.println("Serial connection - couldn't open port");
			e.printStackTrace();
		}
        
        System.out.println("going to sleep for a second..");
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the connection to the printer
	 */
	public void closeConnection()
	{
		connection.closePort();
	}
	
	/**
	 * Sends an array of gcode commands to the printer.
	 */
	public void sendCommands(ArrayList<String> commands)
	{
		for (String command : commands)
		{
			sendCommand(command);
		}
	}

	/**
	 * Sends an single gcode command to the printer.
	 */
	public void sendCommand(String command)
	{
		connection.sendStringToComm(command + commandTerminator);
	}
	
	/* OLD TEST CODE - opens a port and sends a single command
	public static void main(String[] args) {
        System.out.println("Starting communicator"); // Display the string.
        System.out.println("Opening port..");
 
        List<String> ports = getAvailablePorts();
        
        for (String port : ports)
        {
        	System.out.println("portname: "+port);	
        }
        
        String portName = "/dev/cu.usbmodem1421";
        int baudRate = 115200;
        
        SerialConnection connection = new SerialConnection();
        try {
			connection.openPort(portName, baudRate);
		} catch (Exception e) {
			System.out.println("Serial connection - couldn't open port");
			e.printStackTrace();
		}
        
        System.out.println("going to sleep for 3 seconds..");
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("woke up from sleep, about to send g28");
        
        connection.sendStringToComm("G1 Z150\n");
        connection.closePort();
    }
	*/
	
	public static List<String> getAvailablePorts() {

	    List<String> list = new ArrayList<String>();

	    Enumeration portList = CommPortIdentifier.getPortIdentifiers();

	    while (portList.hasMoreElements()) {
	        CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
	        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	            list.add(portId.getName());
	        }
	    }

	    return list;
	}
}