/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks;

import camsucks.model.ComputerModel;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * This class is the controller of this Project. It initialises the Interactive
 * UI elements When its model is set it adds values to certain UI elements and
 * starts a pollAndCompute thread
 *
 *
 * @author Roel
 */
public class FXMLViewController implements Initializable, Runnable {

    //<editor-fold defaultstate="collapsed" desc="Data members">
    private Thread t;
    
    private ComputerModel model;

    @FXML
    private TextField targetRPM;
    @FXML
    private TextField targetCPUTemp;
    @FXML
    private TextField maxCPUTemp;
    @FXML
    private TextField targetGPUTemp;
    @FXML
    private TextField maxGPUTemp;
    @FXML
    private TextField minRPM;
    @FXML
    private ChoiceBox portMap;

    @FXML
    private CheckBox manualCheck;
    @FXML
    private Slider manualSlider;

    @FXML
    private Tab MonitorTab;

    @FXML
    private CheckBox monitorCheck;
    @FXML
    private Label VoltageLabel;
    @FXML
    private Label CPULabel;
    @FXML
    private Label CPULabelMax;
    @FXML
    private Label GPULabel;
    @FXML
    private Label GPULabelMax;
    @FXML
    private Label PowerLabel;
    @FXML
    private Label AMPLabel1;
    @FXML
    private Label AMPLabel2;
    @FXML
    private Label AMPLabel3;
    @FXML
    private Label AMPLabel4;
    @FXML
    private Label AMPLabel5;
    @FXML
    private Label AMPLabel6;

    @FXML
    private Label RPMLabel1;
    @FXML
    private Label RPMLabel2;
    @FXML
    private Label RPMLabel3;
    @FXML
    private Label RPMLabel4;
    @FXML
    private Label RPMLabel5;
    @FXML
    private Label RPMLabel6;
//</editor-fold>

    /*private void closeMonitor(Event event) {
    
    if (MonitorTab.isSelected() && monitorCheck.isSelected() ) {
    //System.out.println(" opened monitor");
    model.setExtraPoll(true);
    
    } else {
    
    //System.out.println(" open configure");
    model.setExtraPoll(false);
    }
    
    }*/
    private void setTargetRPM(ActionEvent event) {
        double dTargetRPM = Double.parseDouble(targetRPM.getText());

        model.setTargetRPM(dTargetRPM);

        //System.out.println(model.getTargetRPM());
    }

    private void setMonitor(ActionEvent event) {

        if (monitorCheck.isSelected()) {

            model.setExtraPoll(true);

        } else {

            model.setExtraPoll(false);

        }

    }

    private void setManual(ActionEvent event) {

        if (manualCheck.isSelected()) {

            model.setManual(true);

            model.setPercentageToSend((int) manualSlider.getValue());

            //System.out.println("manual ");
        } else {

            model.setManual(false);
            // System.out.println("auto");

        }

    }

    private void manualSpeed(MouseEvent event) {

        //System.out.println("miuse event");
        if (model.isManual()) {

            try {
                model.setPercentageToSend((int) manualSlider.getValue());
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void setTargetCPUTemp(ActionEvent event) {
        double dTargetTemp = Double.parseDouble(targetCPUTemp.getText());

        model.setTargetCPUTemp(dTargetTemp);

        //System.out.println(model.getTargetTemp());
    }
    
    private void setTargetGPUTemp(ActionEvent event) {
        double dTargetTemp = Double.parseDouble(targetGPUTemp.getText());

        model.setTargetGPUTemp(dTargetTemp);

        //System.out.println(model.getTargetTemp());
    }

    private void setMinRPM(ActionEvent event) {
        int dMinRPM = Integer.parseInt(minRPM.getText());

        model.setMinRPM(dMinRPM);

        // System.out.println(model.getMaxTemp());
    }
    
    private void setMaxCPUTemp(ActionEvent event) {
        double dMaxTemp = Double.parseDouble(maxCPUTemp.getText());

        model.setMaxCPUTemp(dMaxTemp);

        // System.out.println(model.getMaxTemp());
    }
    
    private void setMaxGPUTemp(ActionEvent event) {
        double dMaxTemp = Double.parseDouble(maxGPUTemp.getText());

        model.setMaxGPUTemp(dMaxTemp);

        // System.out.println(model.getMaxTemp());
    }

    private void setPort(Event event) {

        String selectedPort = portMap.getSelectionModel().selectedItemProperty().getValue().toString();

        model.getGrid().disconnect();

        model.setGrid(selectedPort);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //MonitorTab.setOnSelectionChanged(this::closeMonitor);

        monitorCheck.setOnAction(this::setMonitor);

        targetRPM.setOnAction(this::setTargetRPM);

        targetCPUTemp.setOnAction(this::setTargetCPUTemp);

        maxCPUTemp.setOnAction(this::setMaxCPUTemp);
        
        targetGPUTemp.setOnAction(this::setTargetGPUTemp);

        maxGPUTemp.setOnAction(this::setMaxGPUTemp);
        
        minRPM.setOnAction(this::setMinRPM);

        manualCheck.setOnAction(this::setManual);

        manualSlider.setMax(100);

        manualSlider.setMin(0);

        manualSlider.setOnMouseReleased(this::manualSpeed);

        portMap.setOnAction(this::setPort);

    }

    /**
     * This method sets the model for this controller. After the model is set
     * certain UI elements are updated Finally a pollAndCompute Thread is
     * started
     *
     * @param model the model to set
     */
    public void setModel(ComputerModel model) {

        this.model = model;
        //this.model.setGrid("COM5");

        for (Object key : model.getGrid().getCommunicator().getPortMap().keySet()) {
            portMap.getItems().add(key);
        }

        maxCPUTemp.setText(Double.toString(model.getMaxCPUTemp()));
        maxGPUTemp.setText(Double.toString(model.getMaxGPUTemp()));

        targetRPM.setText(Double.toString(model.getTargetRPM()));

        targetCPUTemp.setText(Double.toString(model.getTargetCPUTemp()));
        targetGPUTemp.setText(Double.toString(model.getTargetGPUTemp()));

        minRPM.setText(Double.toString(model.getMinRPM()));

        updateProperties();

        //pollAndCompute poll = new pollAndCompute(model);
        t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    /**
     * This method updates the values of some UI elements and binds properties
     * to others
     *
     */
    public void updateProperties() {
        
        DecimalFormat df = new DecimalFormat("#.##");      
        
        CPULabel.setText(df.format(model.getSensor().getCPUTemp()) + " °C");
        
        PowerLabel.setText(df.format(model.getGrid().getTotalWattage()) + " W");;
        
        CPULabelMax.setText(df.format(model.getSensor().getCpuMax()) + " °C Max");
        
        GPULabel.setText(df.format(model.getSensor().getGPUTemp()) + " °C");
        
        GPULabelMax.setText(df.format(model.getSensor().getGpuMax()) + " °C Max");
        
        VoltageLabel.setText(df.format(model.getGrid().getVoltage()) + " V");
        
        (RPMLabel1).setText(df.format(model.getGrid().getFanRPM(1)) + " RPM");

        (RPMLabel2).setText(df.format(model.getGrid().getFanRPM(2)) + " RPM");

        (RPMLabel3).setText(df.format(model.getGrid().getFanRPM(3)) + " RPM");

        (RPMLabel4).setText(df.format(model.getGrid().getFanRPM(4)) + " RPM");

        (RPMLabel5).setText(df.format(model.getGrid().getFanRPM(5)) + " RPM");

        (RPMLabel6).setText(df.format(model.getGrid().getFanRPM(6)) + " RPM");

        (AMPLabel1).setText(df.format(model.getGrid().getFanAMP(1)) + " A");

        (AMPLabel2).setText(df.format(model.getGrid().getFanAMP(2)) + " A");

        (AMPLabel3).setText(df.format(model.getGrid().getFanAMP(3)) + " A");

        (AMPLabel4).setText(df.format(model.getGrid().getFanAMP(4)) + " A");

        (AMPLabel5).setText(df.format(model.getGrid().getFanAMP(5)) + " A");

        (AMPLabel6).setText(df.format(model.getGrid().getFanAMP(6)) + " A");

    }

    @Override
    public void run() {

        while (true) {

            long milis = System.currentTimeMillis();

            //System.out.println("Polling...");

            model.poll();

            Platform.runLater(() -> {
                updateProperties();
            });

            //System.out.println("Computing...");
            if (!model.isManual()) {
                model.compute();
            }

            model.getGrid().setFanSpeed(model.getPercentageToSend());

            try {
                while (System.currentTimeMillis() - milis <= 1000) {
                    Thread.sleep(50);
                }
            } catch (InterruptedException ex) {
                System.out.println("Thread got interrupted");
            }
        }

    }

}
