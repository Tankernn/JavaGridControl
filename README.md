# JavaGridControl
[![Build Status](https://tankernn.eu/jenkins/buildStatus/icon?job=JavaGridControl)](https://tankernn.eu/jenkins/job/JavaGridControl)

A Java-based, open-source alternative to the default control software for the NZXT GRID+ and GRID+v2.

## Usage
1. If you are running Windows, download and install [OpenHardwareMonitor](http://openhardwaremonitor.org/).
2. Download the latest version of JavaGridControl from [Jenkins](https://tankernn.eu/jenkins/job/JavaGridControl).
3. Run the jar-file. (append 'nogui' to command line for headless mode)
  * Currently, configuration is only possible through the GUI, though your settings will persist even if you run in headless mode after configuring.
4. Select appropriate COM-port using the combo box at the top.
5. Use Settings -> Configure sensors... to select which sensors to read CPU and GPU temperatures from.
5. When the window is closed, the program will keep running in the background. Use the system tray icon to exit completely.

## Credit
- This project was originally a fork of [RoelGo/CamSucks](https://github.com/RoelGo/CamSucks)
- The reverse engineering of the GRID+ communication was done by rizvanrp, their site is no longer available but here is a screenshot of their article on the GRID+. http://research.domaintools.com/research/screenshot-history/rizvanrp.com/#0
- Some of the serial command codes and GUI ideas were taken from [akej74/grid-control](https://github.com/akej74/grid-control).
- The sensor data on windows systems is read with the help of the jWMI class made by Henry Ranch @ http://henryranch.net
- On windows systems, this class communicates with an external program called openhardwaremonitor @ http://openhardwaremonitor.org/
- This project uses [SystemTray](https://github.com/dorkbox/SystemTray).

## TODO
- Unit tests
- Start on boot for Windows and Linux