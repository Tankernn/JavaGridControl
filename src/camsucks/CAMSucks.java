/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks;

import java.awt.Toolkit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *  This Project aims to replace the default software made for the GRID+ Controller; CAM
 * <p>
 * Software used for this project
 * <p>
 * <ul>
 * <li> The reverse engineering of the GRID+ communication was done by rizvanrp, their site is no longer available but here is a screenshot of their article on the GRID+ http://research.domaintools.com/research/screenshot-history/rizvanrp.com/#0
 * <li> The aim is to be able to control the fan speed of the fans connected to a GRID controller according to the temperature of the CPU Packages
 * <li> The project uses a Communicator class that has been created by Henry Poon @ https://blog.henrypoon.com/ 
 * <li> With the help of this class the communication with the GRID+ controller is handled
 * <li> The sensor data is read with the help of the jWMI class made by Henry Ranch @ http://henryranch.net
 * <li> This class communicates with an external program called openhardwaremonitor @ http://openhardwaremonitor.org/
 * </ul>
 * <p>
 * 
 *  Currently monitoring is a bit bugged and is disabled by default but can be turned on with a checkbox.
 * <p>
 *  Future plans and TODOs:
 * <p>
 * <ul>
 * <li>     Make it possible to control fans according to GPU or CPU  or Both temperatures (seems easy enough).
 * <li>     Add Integral control to achieve full PI control (Before this can happen a the time constant of the system must be defined reliably .
 * <li>     Make program not crash after/during system sleep/hibernation.
 * <li>     Find a way to compile program and not get security warnings (because of the filewriter in the jWMI class).
 * <li>     Make a config file to save user setting in.
 * </ul>
 * <p>
 * 
 * 
 * @author Roel
 */
public class CAMSucks extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        
        // Model
        ComputerModel model = new ComputerModel("COM3");
        
        // View

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FXMLView.fxml"));
        Parent root = loader.load();
        
        
        // Controller
        FXMLViewController controller = loader.getController();
        controller.setModel(model);
 
        Scene scene = new Scene(root);
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        stage.setResizable(false);
        stage.setTitle("CAM Sucks!");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("NoCAMIcon.jpg")));
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest(event -> {
            model.getGrid().disconnect();
            System.exit(0);
        });
        
    }

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        launch(args);

    }

}
