import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.willwinder.universalgcodesender.SerialConnection;

public class SerialCommunicator {
    
	public static void main(String[] args) {
        System.out.println("Starting communicator"); // Display the string.
        System.out.println("Opening port..");
 
        List<String> ports = getAvailablePorts();
        
        for (String port : ports)
        {
        	System.out.println("portname: "+port);	
        }
        
        String portName = "TODO";
        
        SerialConnection connection = new SerialConnection();
        try {
			connection.openPort(portName, 9600);
		} catch (Exception e) {
			System.out.println("Serial connection - couldn't open port");
			e.printStackTrace();
		}
    }
	
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