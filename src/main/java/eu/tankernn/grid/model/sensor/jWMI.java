package eu.tankernn.grid.model.sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.naming.InvalidNameException;

/**
 * File: jWMI.java Date: 12/21/09 Author: Shaun Henry Copyright Henry Ranch LLC
 * 2009-2010. All rights reserved. http://www.henryranch.net
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + You must provide a link back to www.henryranch.net in any software or
 * website which uses this software. + Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. + Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. +
 * Neither the name of the HenryRanch LCC nor the names of its contributors nor
 * authors may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS, OWNERS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * A java bridge for querying the WMI interface.
 *
 * @author Copyright 2009-2010 HenryRanch LLC. Author Shaun Henry, 2009-2010.
 * @version 1.0
 * 
 * Changes by Roel:     rewritten the getVBScript function and added two variants getVBScriptList and getVBScriptValue to work with the OpenHardwareMonitor WMI Classes
 *                      rewritten the getWMIValue and added another variant getWMISensorList
 *                      Changed some demoqueries for testing purposes.
 *                      
 *                 
 *
 */
public class jWMI {

    private static final String CRLF = "\r\n";

    /**
     * Generate a VBScript string capable of querying the desired WMI
     * information.
     *  In this case a list of sensors of a certain type
     * @param SensorType The sensorType to get a list form.
     * <br>i.e. "Load"
     * @return the vbscript string.
     *
     */
    private static String getVBScriptList(String SensorType) {
        String vbs = "strComputer = \".\"" + CRLF;
        vbs += "strNameSpace = \"root\\OpenHardwareMonitor\"" + CRLF;
        vbs += "Dim oWMI : Set oWMI = GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\\" & strNameSpace )" + CRLF;
        vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\"Select * from Sensor\")" + CRLF;
        vbs += "Dim obj, strData" + CRLF;
        vbs += "For Each obj in classComponent" + CRLF;
        vbs += "    If obj.SensorType = \"" + SensorType + "\" then" + CRLF;
        vbs += "            strData = strData & obj.Name & \", \" " + CRLF;
        vbs += "    End If" + CRLF;
        vbs += "Next" + CRLF;
        vbs += "wscript.echo strData" + CRLF;
        return vbs;
    }

    
    
    /**
     * Generate a VBScript string capable of querying the desired WMI
     * information.
     * 
     * in this case the the Value of a sensor with name name and sensor type SensorType
     *
     * @param SensorType The type of the sensor
     * <br>i.e. "Temperature"
     * @param name the name of the sensor
     * <br>i.e. "CPU Package"
     * @return the vbscript string.
     *
     */
    private static String getVBScriptValue(String SensorType, String name) {
        String vbs = "strComputer = \".\"" + CRLF;
        vbs += "strNameSpace = \"root\\OpenHardwareMonitor\"" + CRLF;
        vbs += "Dim oWMI : Set oWMI = GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\\" & strNameSpace )" + CRLF;
        vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\"Select * from Sensor\")" + CRLF;
        vbs += "Dim obj, strData" + CRLF;
        vbs += "For Each obj in classComponent" + CRLF;
        vbs += "    If obj.SensorType = \"" + SensorType + "\" then" + CRLF;
        vbs += "        If obj.Name = \"" + name + "\" then" + CRLF;
        vbs += "            strData = strData & obj.Value" + CRLF;
        vbs += "        End If" + CRLF;
        vbs += "    End If" + CRLF;
        vbs += "Next" + CRLF;
        vbs += "wscript.echo strData" + CRLF;
        return vbs;
    }
    
    /**
     * Generate a VBScript string capable of querying the desired WMI
     * information.
     * 
     * in this case the the Value of a sensor with name name and sensor type SensorType
     *
     * @param SensorType The type of the sensor
     * <br>i.e. "Temperature"
     * @param name the name of the sensor
     * <br>i.e. "CPU Package"
     * @return the vbscript string.
     *
     */
    
    private static String getVBScriptValue(String SensorType, String name, String value) {
        String vbs = "strComputer = \".\"" + CRLF;
        vbs += "strNameSpace = \"root\\OpenHardwareMonitor\"" + CRLF;
        vbs += "Dim oWMI : Set oWMI = GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\\" & strNameSpace )" + CRLF;
        vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\"Select * from Sensor\")" + CRLF;
        vbs += "Dim obj, strData" + CRLF;
        vbs += "For Each obj in classComponent" + CRLF;
        vbs += "    If obj.SensorType = \"" + SensorType + "\" then" + CRLF;
        vbs += "        If obj.Name = \"" + name + "\" then" + CRLF;
        vbs += "            strData = strData & obj." + value + CRLF;
        vbs += "        End If" + CRLF;
        vbs += "    End If" + CRLF;
        vbs += "Next" + CRLF;
        vbs += "wscript.echo strData" + CRLF;
        return vbs;
    }
    
    /**
     * Get an environment variable from the windows OS
     *
     * @param envVarName the name of the env var to get
     * @return the value of the env var
     * @throws IOException 
     * @throws InvalidNameException if the given envVarName does not exist
     *
     */
    private static String getEnvVar(String envVarName) throws IOException, InvalidNameException {
        String varName = "%" + envVarName + "%";
        String envVarValue = execute(new String[]{"cmd.exe", "/C", "echo " + varName});
        if (envVarValue.equals(varName)) {
            throw new InvalidNameException("Environment variable '" + envVarName + "' does not exist!");
        }
        return envVarValue;
    }

    /**
     * Write the given data string to the given file
     *
     * @param filename the file to write the data to
     * @param data a String ofdata to be written into the file
     * @throws IOException 
     * @throws Exception if the output file cannot be written
     *
     */
    private static void writeStrToFile(String filename, String data) throws IOException {
        FileWriter output = new FileWriter(filename);
        output.write(data);
        output.flush();
        output.close();
        output = null;
    }

    /**
     * Get the given List of WMI names of sensors of sensor type SensorType
     *
     * @param SensorType The type of sensor to get a list of
     * @return a comma separated list of the sensors
     * @throws IOException 
     * @throws Exception if there is a problem obtaining the value
     *
     */
    public static String getWMISensorList(String SensorType) throws IOException {
        String vbScript = getVBScriptList(SensorType);
        
        String output = execute(new String[]{"cmd.exe", "/C", "cscript.exe", writeToTempFile(vbScript)});
        //new File(tmpFileName).delete();

        return output.trim();
    }
     /**
     * Get the given WMI value from sensor with name nam and type SensorType
     *
     * @param SensorType the type of the sensor
     * @param name the name of the sensor
     * @return the value of the sensor
     * @throws IOException 
     * @throws Exception if there is a problem obtaining the value
     *
     */
    public static String getWMIValue(String SensorType, String name) throws IOException {
        String vbScript = getVBScriptValue(SensorType, name);
        String output = execute(new String[]{"cmd.exe", "/C", "cscript.exe", writeToTempFile(vbScript)});
        //new File(tmpFileName).delete();

        return output.trim();
    }
    
    /**
     * Get the given WMI value from sensor with name nam and type SensorType
     *
     * @param SensorType the type of the sensor
     * @param name the name of the sensor
     * @return the value of the sensor
     * @throws IOException 
     * @throws Exception if there is a problem obtaining the value
     *
     */
    public static String getWMIValue(String SensorType, String name, String value) throws IOException {
        String vbScript = getVBScriptValue(SensorType, name, value);
        String output = execute(new String[]{"cmd.exe", "/C", "cscript.exe", writeToTempFile(vbScript)});
        //new File(tmpFileName).delete();

        return output.trim();
    }

    /**
     * Execute the application with the given command line parameters.
     *
     * @param cmdArray an array of the command line params
     * @return the output as gathered from stdout of the process
     * @throws IOException 
     * @throws an Exception upon encountering a problem
     *
     */
    private static String execute(String[] cmdArray) throws IOException {
        Process process = Runtime.getRuntime().exec(cmdArray);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = "";
        String line = "";
        while ((line = input.readLine()) != null) {
            //need to filter out lines that don't contain our desired output
            if (!line.contains("Microsoft") && !line.equals("")) {
                output += line + CRLF;
            }
        }
        process.destroy();
        process = null;
        return output.trim();
    }
    
    private static String writeToTempFile(String data) throws IOException {
    	String tmpDirName;
		try {
			tmpDirName = getEnvVar("TEMP").trim();
		} catch (InvalidNameException | IOException e) {
			tmpDirName = "/temp";
		}
        String tmpFileName = tmpDirName + File.separator + "jwmi.vbs";
        writeStrToFile(tmpFileName, data);
        return tmpFileName;
    }

    /**
     *
     */
    public static void executeDemoQueries() {
        try {
            System.out.println(Float.parseFloat(getWMIValue("Temperature", "CPU Core #3")));
            System.out.println(getWMISensorList("Temperature"));
            //System.out.println(getWMIValue("Select Description from Win32_PnPEntity", "Description"));
            //System.out.println(getWMIValue("Select Description, Manufacturer from Win32_PnPEntity", "Description,Manufacturer"));
            //System.out.println(getWMIValue("Select * from Win32_Service WHERE State = 'Stopped'", "Name"));
            //this will return everything since the field is incorrect and was not used to a filter
            //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
            //this will return nothing since there is no field specified
            //System.out.println(getWMIValue("Select Name from Win32_ComputerSystem", ""));
            //this is a failing case where the Win32_Service class does not contain the 'Name' field
            //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	executeDemoQueries();
    }
}
//WMI class definitions below here: 

