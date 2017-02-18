# JavaGridControl
A Java-based, open-source alternative to the default control software for the NZXT GRID+ and GRID+v2.

## Credit
- This project was originally a fork of ![RoelGo/CamSucks]()
- The reverse engineering of the GRID+ communication was done by rizvanrp, their site is no longer available but here is a screenshot of their article on the GRID+. http://research.domaintools.com/research/screenshot-history/rizvanrp.com/#0
- Some of the serial command codes were taken from akej74/grid-control
- The sensor data on windows systems is read with the help of the jWMI class made by Henry Ranch @ http://henryranch.net
- On windows systems, this class communicates with an external program called openhardwaremonitor @ http://openhardwaremonitor.org/

##TODO
- Make a config file to save user settings in.
- Make it possible to control fans according to the temperatures of GPU, CPU or both.
- Manual and profile-based fan speeds.
