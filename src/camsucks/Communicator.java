/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks;

import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * This class communicates with a serial com device
 * @author Henry Poon  @ https://blog.henrypoon.com/ 
 * Changes by Roel:     Removed all references to the GUI class from Henry Poon's original project.
 *                      Changed the event based serial read to a synchronised read after each write. 
 *                      Added a Buffer for the serial read so that the read data can easily be accessed by other classes
 *                      Completely rewritten the writeData method so that it works with the GRID+ controller
 *                      Added some getters and setters for the buffer
 *                      added a getter for the portMap so that the controller and view can access it
 */
public class Communicator {

    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();
    //this is the object that contains the opened port
    private String selectedPort;
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //Buffer
    private int leng;
    private byte[] buffer;

    //a string for recording what goes on in the program
    String logText = "";

    /**
     *
     * @param selectedPort This parameter is the string name of the port the communicator object should connect to
     */
    public Communicator(String selectedPort) {
        this.selectedPort = selectedPort;
    }

    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI

    /**
     *
     * This method searches for COM ports on the system and saves their Identifier in the hashmap portMap with their name as key.
     * 
     */
    public void searchForPorts() {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                getPortMap().put(curPort.getName(), curPort);
            }
        }
        //System.out.println(portMap);
    }

    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated

    /**
     *This method opens the COM port with port parameters: Baudrate: 4800; databits: 8; Stopbit: 1; parity: none; 
     */
    public void connect() {
        selectedPortIdentifier = (CommPortIdentifier) getPortMap().get(selectedPort);

        CommPort commPort = null;

        try {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            //for controlling GUI elements
            setConnected(true);

            //logging
            logText = selectedPort + " opened successfully.";
            System.out.println(logText);
            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

        } catch (PortInUseException e) {
            logText = selectedPort + " is in use. (" + e.toString() + ")";

            System.out.println(logText);
        } catch (Exception e) {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            System.out.println(logText);
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized intput and output streams for use to communicate data

    /**
     * This method initializes the serial IO stream,
     * after the init is complete the method sends an initialize command to the GRID+ controller
     * @return successful Boolean value which indicates whether the method was completed succesfuly
     */
    public boolean initIOStream() {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

            writeData(0Xc0);
            successful = true;
            return successful;
        } catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            System.out.println(logText);
            return successful;
        }
    }

    
    /**
     *This method disconnects the serial communication by first closing the serialPort and then closing the IOStreams
     * 
     * 
     */
    public void disconnect() {
        //close the serial port
        try {
           
            
            //serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);

            logText = "Disconnected.";
            System.out.println(logText);
        } catch (IOException e) {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            System.out.println(logText);
        }
    }

    /**
     *  Returns true if the connection is open
     *  False if closed
     * @return boolean value for the connection status
     */
    final public boolean getConnected() {
        return bConnected;
    }

    /**
     *Setter for the  boolean value for the connection status
     * @param bConnected boolean value for the connection status
     */
    public void setConnected(boolean bConnected) {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads

    /**
     * This method reads data from the input stream and puts it in a buffer 
     * after the data is read the method waits 50 msec to make sure a new command doesn't follow up to quickly
     * This promotes stability in the program.
     * 
     * 
     */
    public void serialRead() throws InterruptedException {
        try {
            if (input.available() > 0) {
                try {

                    setBuffer(new byte[1024]);

                    setLeng(input.read(getBuffer()));

                    // Debug.
                    /*System.out.println("Length: " + getLeng());
            
                    for (int itera = 0; itera < leng; itera++) {
                    System.out.println("byte " + itera + ": " + Integer.toHexString(getBuffer(itera) & 0xFF));
                    }*/
                } catch (Exception e) {
                    logText = "Failed to read data. (" + e.toString() + ")";
                    System.out.println(logText);
                }
            } else {
                logText = "Failed to read data. No data to read";
                //System.out.println(logText);
            }

        } catch (IOException e) {
            logText = "Failed to read data. (" + e.toString() + ")";
            System.out.println(logText);
        }
        //This prevents commands from being send too soon  
        sleep(50);
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device

    /**
     * This method sends a single byte command over the serial communication afterwards the method calls the read method
     * @param command a single byte command
     */
    public void writeData(int command) {
        try {
            output.write(command);
            sleep(20);
            serialRead();
        } catch (Exception e) {
            logText = "Failed to write data. (" + e.toString() + ")";
            System.out.println(logText);

        }
    }

    /**
     *  This method sends a byte array command over the serial communication afterwards the method calls the read method
     * @param command an array of bytes as a command
     */
    public void writeData(byte[] command) {
        try {
            output.write(command);
            sleep(50);
            serialRead();
        } catch (Exception e) {
            logText = "Failed to write data. (" + e.toString() + ")";
            System.out.println(logText);

        }
    }

    /**
     * @return the leng
     */
    public int getLeng() {
        return leng;
    }

    /**
     * @param leng the leng to set
     */
    public void setLeng(int leng) {
        this.leng = leng;
    }

    /**
     * @return the buffer
     */
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     *
     * @param i 
     * @return The value of the buffer at i
     */
    public byte getBuffer(int i) {
        return buffer[i];
    }

    /**
     *
     * @return  The second to last byte of the buffer
     */
    public byte getSecondToLast() {
        if (leng >= 2) {
            return buffer[leng - 2];
        } else {
            return 0;
        }
    }

    /**
     *
     * @return The last byte of the buffer
     */
    public byte getlast() {
        if (leng >= 2) {
            return buffer[leng - 1];
        } else {
            return 0;
        }
    }

    /**
     * @param buffer the buffer to set
     */
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    /**
     * @return the portMap
     */
    public HashMap getPortMap() {
        return portMap;
    }

}
