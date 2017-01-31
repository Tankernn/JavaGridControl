/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the model of this project
 *
 * This model contains two main data members as well as some data members used
 * to configure the calculations and two checks
 *
 * Along with all the getters and setters this model has two methods used for
 * polling and calculating
 *
 * @author Roel
 */
public class ComputerModel {

    // The main components of the Computermodel
    private Sensor sensor;
    private GRID grid;

    // The variables used to calculate the percentage to send
    private double maxCPUTemp;
    private double maxGPUTemp;
    private double targetCPUTemp;
    private double targetGPUTemp;
    private double targetRPM;
    // a global minimum percentage, this is used to prevent the controller to constantly turn the fans on and off
    private int minRPM;

    //Boolean Checks used to know whether to run certain pieces of code
    private boolean extraPoll;
    private boolean manual;
    // private boolean GRIDset;
    // The percentage to send at the end of the loop.   
    private int percentageToSend;
    private int ticks;

    /**
     *
     *
     * All members get initialised here.
     *
     * @param selectedPort
     */
    public ComputerModel(String selectedPort) {
        targetRPM = 35;
        targetCPUTemp = 50;
        maxCPUTemp = 70;
        targetGPUTemp = 50;
        maxGPUTemp = 70;
        minRPM = 35;
        extraPoll = true;
        manual = false;
        ticks = 0;

        grid = new GRID(selectedPort);

        try {
            sensor = new Sensor();
        } catch (Exception ex) {
            Logger.getLogger(ComputerModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * Currently only the pollCPUPackageTemp method of the sensor object is
     * called This can change in the future
     *
     */
    public void poll() {
        //System.out.println("Polling...");

        if (isExtraPoll()) {
            ticks++;
            if (!getGrid().isSame() || ticks > 10) {
                //System.out.println("polling some extra shit ");
                ticks = 0;

                getGrid().pollVoltage();
                

                for (int i = 1; i < 7; i++) {
                    getGrid().pollFanAMP(i);
                    getGrid().pollFanRPM(i);

                }
            }

        }

        try {
            getSensor().pollCPUTemp();
            getSensor().pollCPUMax();
            getSensor().pollGPUTemp();
            getSensor().pollGPUMax();
            
            
        } catch (Exception ex) {
            System.out.println("Polling Failed");
        }

    }

    /**
     *
     * In this method the percentage to sent is computed. The calculation aims
     * to act as a proportional controller. A later step could be to add an
     * integral controller to the calculation to get a better calculated fan
     * curve
     *
     */
    public void compute() {

        double currentCPUTemp = getSensor().getCPUTemp();

        double CPUerror = (currentCPUTemp - getTargetCPUTemp());

        double CPUkFactor = ((100 - getTargetRPM()) / (getMaxCPUTemp() - getTargetCPUTemp()));

        int tempCPUPercentage = (int) (getTargetRPM() + (CPUkFactor * CPUerror));

        if (tempCPUPercentage < minRPM) {
            tempCPUPercentage = minRPM;
        }
        
        double currentGPUTemp = getSensor().getGPUTemp();

        double GPUerror = (currentGPUTemp - getTargetGPUTemp());

        double GPUkFactor = ((100 - getTargetRPM()) / (getMaxGPUTemp() - getTargetGPUTemp()));

        int tempGPUPercentage = (int) (getTargetRPM() + (GPUkFactor * GPUerror));

        if (tempGPUPercentage < minRPM) {
            tempGPUPercentage = minRPM;
        }
        
        if(tempCPUPercentage > tempGPUPercentage){
            setPercentageToSend(tempCPUPercentage);
        }   else    {
            setPercentageToSend(tempGPUPercentage);
        }

    }

    /**
     * A getter for the Sensor Object to make the methods of the object
     * available
     *
     * @return the sensor
     */
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * A getter for the GRID Object to make the methods of the object available
     *
     * @return the grid
     */
    public GRID getGrid() {
        return grid;
    }

    /**
     * This setter overwrites the old GRID with a new Object with the
     * selectedport as the COM port to connect to
     *
     * @param selectedPort The com port the GRID controller is located at
     */
    public void setGrid(String selectedPort) {
        grid = new GRID(selectedPort);
    }

    /**
     * Used to check if the extra polling has to be exicuted
     *
     * @return the extraPoll
     */
    public boolean isExtraPoll() {
        return extraPoll;
    }

    /**
     * Used to define whether the extra polling must be done
     *
     * @param extraPoll the extraPoll to set
     */
    public void setExtraPoll(boolean extraPoll) {
        this.extraPoll = extraPoll;
    }

    /**
     *
     * @return the targetRPM
     */
    public double getTargetRPM() {
        return targetRPM;
    }

    /**
     * @param targetRPM the targetRPM to set
     */
    public void setTargetRPM(double targetRPM) {
        this.targetRPM = targetRPM;
    }

    /**
     * @return the targetTemp
     */
    public double getTargetCPUTemp() {
        return targetCPUTemp;
    }

    /**
     * @param targetTemp the targetTemp to set
     */
    public void setTargetCPUTemp(double targetTemp) {
        this.targetCPUTemp = targetTemp;
    }

    /**
     * @return the maxTemp
     */
    public double getMaxCPUTemp() {
        return maxCPUTemp;
    }

    /**
     * @param maxTemp the maxTemp to set
     */
    public void setMaxCPUTemp(double maxTemp) {
        this.maxCPUTemp = maxTemp;
    }
    /**
     * @return the targetTemp
     */
    public double getTargetGPUTemp() {
        return targetGPUTemp;
    }

    /**
     * @param targetTemp the targetTemp to set
     */
    public void setTargetGPUTemp(double targetTemp) {
        this.targetGPUTemp = targetTemp;
    }

    /**
     * @return the maxTemp
     */
    public double getMaxGPUTemp() {
        return maxGPUTemp;
    }

    /**
     * @param maxTemp the maxTemp to set
     */
    public void setMaxGPUTemp(double maxTemp) {
        this.maxGPUTemp = maxTemp;
    }

    /**
     * A check to see whether the percentage to send is manually set or has to
     * be calculated
     *
     * @return the manual
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * @param manual the manual to set
     */
    public void setManual(boolean manual) {
        this.manual = manual;
    }

    /**
     * @return the minRPM
     */
    public double getMinRPM() {
        return minRPM;
    }

    /**
     *
     * @param minRPM
     */
    public void setMinRPM(int minRPM) {
        this.minRPM = minRPM;
    }

    /**
     * @return the percentageToSend
     */
    public int getPercentageToSend() {
        return percentageToSend;
    }

    /**
     * @param percentageToSend the percentageToSend to set
     */
    public void setPercentageToSend(int percentageToSend) {
        this.percentageToSend = percentageToSend;
    }
    
    

}
