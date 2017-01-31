/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks.model;

import java.util.Arrays;
import java.util.HashMap;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * This is a model for the GRID controller 
 * This class uses the communicator class to communicate to the GRID+ controller
 * This class contains a few data members that contain data that can be gathered from the GRID+
 * This class has some getters for these members 
 * To update their values they each have poll functions instead of setters which send a command to the GRID and read the response
 * To set the fan speed this class has a setFanSpeed method which sets the voltage of the GRID+ according to a percentage input.
 * 
 * This class also has a boolean check that checks if the voltage command is the same as the previous voltage command, this is to prevent pointless serial communication
 * This check also has a getter to make it usable for other classes 
 * 
 * @author Roel
 */
public class GRID {
    
    private HashMap portMap = new HashMap();

    private double voltage;

    private int[] fanRPM;

    private double[] fanAMP;
    
    private double totalWattage;

    private byte[] lastCommand;

    private Communicator communicator;
    
    private boolean same;

    /**
     * This constructor initialises  all members
     * afterwards it opens a communicator at the selected port 
     * @param selectedPort the com port to connect to
     */
    public GRID(String selectedPort) {
        
        totalWattage = 0;
        lastCommand = new byte[7];
        lastCommand[5] = 1;
        lastCommand[6] = 2;
        voltage = 0;
        fanAMP = new double[7];
        fanRPM = new int[7];
        
        /*for (int i = 0; i < fanRPM.length; i++) {
        fanRPM[i] = new SimpleIntegerProperty(0);
        }
        
        for (int i = 0; i < fanAMP.length; i++) {
        fanAMP[i] = new SimpleDoubleProperty(0);
        }*/
        
        communicator = new Communicator(selectedPort);
        
        communicator.searchForPorts();
        
        communicator.connect();

        if (communicator.getConnected() == true) {
           communicator.initIOStream();
           //System.out.println(Arrays.toString(communicator.getBuffer()));
          
           
        }
        
    }

    /**
     *This method simply runs the disconnect method of the communicator
     */
    public void disconnect() {

        if (getCommunicator().getConnected() == true) {
            getCommunicator().disconnect();
        }

    }

    /**
     * @return the voltage
     */
    public double getVoltage() {
        return voltage;
    }

    /**
     * @return the fanRPM
     */
    public int[] getFanRPM() {
        return fanRPM;
    }

    /**
     * @return the fanAMP
     */
    public double[] getFanAMP() {
        return fanAMP;
    }

    /**
     * @param fan the index of the fan
     * @return the fanRPM of the fan with index fan
     */
    public int getFanRPM(int fan) {
        return fanRPM[fan - 1];
    }

    /**
     * @param fan the index of the fan
     * @return the fanAMP of the fan with index fan
     */
    public double getFanAMP(int fan) {
        return fanAMP[fan - 1];
    }

    /**
     * This method polls the Fan Amperage of the fan with index fan
     * it first send the poll command and reads the byte data from the buffer
     * this byte data is then converted to a double
     * @param fan the index of the fan
     */
    public void pollFanAMP(int fan) {
        if (getCommunicator().getConnected() == true) {
            double tempAMP;
            // 0x85 = -123
            byte[] command = {-123, (byte) fan};

            getCommunicator().writeData(command);
            
            tempAMP = (double) (((int) getCommunicator().getSecondToLast() & 0xFF) + (((double) (getCommunicator().getlast() & 0xff) / 100)));

            fanAMP[fan - 1] = tempAMP;

        } else {
            fanAMP[fan - 1] =  0;
        }
    }

    /**
     * This method polls the Fan RPM of the fan with index fan
     * it first send the poll command and reads the byte data from the buffer
     * this byte data is then converted to an int
     * @param fan the index of the fan
     */
    public void pollFanRPM(int fan) {
        if (getCommunicator().getConnected() == true) {
            // 0x8A = -118            

            byte[] command = {-118, (byte) fan};

            getCommunicator().writeData(command);

            fanRPM[fan - 1] = (((int) (getCommunicator().getSecondToLast() & 0xFF) << 8) | ((getCommunicator().getlast() & 0xFF)));

        } else {

            fanRPM[fan - 1] =  0;

        }
    }

    /**
     * This method polls the voltage of all the fans 
     * it first send the poll command and reads the byte data from the buffer
     * this byte data is then converted to a double
     */
    public void pollVoltage() {
        if (getCommunicator().getConnected() == true) {
            // 0x84 = -124
            byte[] command = {-124, 0x00};

            getCommunicator().writeData(command);

            voltage = (double) (((int) getCommunicator().getSecondToLast() & 0xFF) + (((double) (getCommunicator().getlast() & 0xff) / 100)));
        } else {
            voltage = 0;
        }
    }

    /**
     * this method calculates the voltage to set the fans at according to a percentage of the maximum voltage 12V
     * then it send the command to set that voltage
     * the voltages between 0 and 4 are not recognised so these are converted to 4V
     * the comma value of the commands are always rounded to .50 and .0
     * @param percent
     */
    public void setFanSpeed(int percent) {

        if (getCommunicator().getConnected() == true) {
            int firstByte, lastByte, wantedVoltage;
            
            //The voltages between 0 and 4 are not recognised by the grid so any voltage under 4 will still be 4 and from 0 it will be 0
            if (percent <= 0) {
                firstByte = 0;
                lastByte = 0;
            
            } else if (percent < 34) {
                firstByte = 4;
                lastByte = 0;
            }else{

                wantedVoltage = (1200 * percent) / 100;
                firstByte = wantedVoltage / 100;
                lastByte = (wantedVoltage - (firstByte * 100));

                if (lastByte < 50) {
                    lastByte = 0x00;
                } else {
                    lastByte = 0x50;
                }

            }

            byte[] command = {0x44, 0x00, -64, 0x00, 0x00, (byte) firstByte, (byte) lastByte};
            
            if ((lastCommand[5] != command[5] || lastCommand[6] != command[6])) {
                getCommunicator().writeData(command);
                lastCommand = command;
                setSame(false);
            } else {
                setSame(true);
            }
            
            

        }

    }

    /*@Override
    protected void finalize() throws Throwable {
    
    try {
    disconnect();
    } finally {
    super.finalize();
    }
    
    }*/
    
    /**
     * @return the communicator
     */
    public Communicator getCommunicator() {
        return communicator;
    }

    /**
     * 
     * @return A boolean value that is true if the last command is the same as the new
     */
    public boolean isSame() {
        return same;
    }

    /**
     * @param same boolean value that is true if the last command is the same as the new
     */
    public void setSame(boolean same) {
        this.same = same;
    }

    /**
     * @return the totalWattage
     */
    public double getTotalWattage() {
        totalWattage = 0;
        for(double tempAMP : fanAMP ){
            totalWattage = totalWattage + tempAMP*getVoltage();
            
        }
        
        
        
        
        return totalWattage;
    }

}
