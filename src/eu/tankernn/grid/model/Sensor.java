package eu.tankernn.grid.model;

import java.io.IOException;

/**
 *
 * This class is a model for a collection of sensors
 * It contains a datamembers for some specific sensors as well as members for lists of sensors of a specific type.
 * each member has a getter but no setter.
 * Instead of the setter the members (except for the lists) have a poll function which polls the value of the member using the jWMI class 
 * 
 * @author Roel
 */
public class Sensor {
    private String[] temperatureSensorList;
    private String[] loadSensorList;
    private int cpuCoreNumber;
    private double cpuLoad;
    private double cpuPackageTemp;
    private double cpuMax;
    private double gpuTemp;
    private double gpuMax;

    /**
     * The constructor for this class
     * At the start the lists of sensors are made with the help of the jWMI class
     * Then the number of cores of the system is calculated
     * @throws IOException when the WMI value can't be obtained
     */
    public Sensor() throws IOException {
        temperatureSensorList = jWMI.getWMISensorList("Temperature").split(", ");
        loadSensorList = jWMI.getWMISensorList("Load").split(", ");
        //System.out.println(Arrays.toString(temperatureSensorList));
        //System.out.println(Arrays.toString(loadSensorList));

        //Init for cpuCoreNumber
        cpuCoreNumber = 0;
        for (String temperatureSensorList1 : loadSensorList) {
            if (temperatureSensorList1.contains("CPU Core #")) {
                int tempNumber;
                // gets the int value of the number after #
                tempNumber = Integer.parseInt(temperatureSensorList1.substring(10, 11).trim());
                if (tempNumber > cpuCoreNumber) {
                    cpuCoreNumber = tempNumber;
                }
            }
        }
    }

    /**
     *  This method polls the value of the CPU Load sensor
     * @throws Exception
     */
    public void pollCPULoad() throws Exception {
        cpuLoad = Double.parseDouble(jWMI.getWMIValue("Load", "CPU Total"));

    }
    
    /**
     *  This method polls the value of the CPU Load sensor
     * @throws NumberFormatException 
     * @throws Exception
     */
    public void pollCPUMax() throws NumberFormatException, Exception {
        cpuMax = Double.parseDouble(jWMI.getWMIValue("Temperature", "CPU Package", "Max"));

    }
    
    /**
     *  This method polls the value of the CPU Load sensor
     * @throws Exception
     */
    public void pollGPUMax() throws Exception {
        gpuMax = Double.parseDouble(jWMI.getWMIValue("Temperature", "GPU Core", "Max"));

    }
    
    /**
     * This method polls the value of the GPU Temperature sensor
     * @throws Exception
     */
    public void pollGPUTemp() throws Exception {
        gpuTemp = Double.parseDouble(jWMI.getWMIValue("Temperature", "GPU Core"));

    }

    /**
     * This method polls the value of the CPU Temperature sensor
     * @throws Exception
     */
    public void pollCPUTemp() throws Exception {
        cpuPackageTemp = Double.parseDouble(jWMI.getWMIValue("Temperature", "CPU Package"));
    }

    /**
     * @return the temperatureSensorList
     */
    public String[] getTemperatureSensorList() {
        return temperatureSensorList;
    }

    /**
     * @return the loadSensorList
     */
    public String[] getLoadSensorList() {
        return loadSensorList;
    }

    /**
     * @return the cpuCoreNumber
     */
    public int getCpuCoreNumber() {
        return cpuCoreNumber;
    }

   
    /**
     * @return the cpuLoad
     */
    public double getCpuLoad() {
        return cpuLoad;
    }

    /**
     * @return the cpuPackageTemp
     */
    public double getCPUTemp() {
        return cpuPackageTemp;
    }

    /**
     * @return the gpuTemp
     */
    public double getGPUTemp() {
        return gpuTemp;
    }

    /**
     * @return the cpuMax
     */
    public double getCpuMax() {
        return cpuMax;
    }
    
     /**
     * @return the cpuMax
     */
    public double getGpuMax() {
        return gpuMax;
    }

}
