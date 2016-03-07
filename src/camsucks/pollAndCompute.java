/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camsucks;

import javafx.application.Platform;

/**
 *
 * This class, when run, enters an infinite loop. This loop will if the right
 * conditions are met first poll the FAN RPM and FAN AMP of each fan Then it
 * will poll the CPU temperature After that if the program isn't set to manual
 * the loop will compute the percentage to send according to the cpu temperature
 * as last step this loop will send the desired percentage to the fans (this
 * could be the computed or the manually chosen percentage
 *
 * @author Roel
 */
class pollAndCompute implements Runnable {

    private ComputerModel model;

    private Thread t;

    private int ticks;

    public pollAndCompute(ComputerModel model) {
        ticks = 0;
        this.model = model;
    }

    @Override
    public void run() {

        while (true) {

            //System.out.println("Polling...");
            if (model.isExtraPoll()) {
                ticks++;
                if (!model.getGrid().isSame() || ticks > 2) {
                    //System.out.println("polling some extra shit ");
                    ticks = 0;
                    Platform.runLater(() -> {
                        for (int i = 1; i < 7; i++) {
                            model.getGrid().pollFanAMP(i);
                            model.getGrid().pollFanRPM(i);

                        }
                    });
                }

            }

            model.poll();

            //System.out.println("Computing...");
            if (!model.isManual()) {
                model.compute();
            }

            model.getGrid().setFanSpeed(model.getPercentageToSend());
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Thread got interrupted");
            }
        }

    }

    /*public void start(){
        System.out.println("Starting");
        if(t == null){
            t = new Thread(this, "Poll and Compute");
            t.start();
        }
    }*/
}
