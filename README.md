Forrest
=======
Forrest runs tasks using a GUI. This weirdware keeps track of tasks, such as the ssh tunnels you have running and any scripts that
may be running locally or on a remote computer over ssh.

Each task is run on its own thread and can be started and stopped using the interface. 
CPU usage should be minimal when running, adhoc tests put CPU usage at around 0.1%. 
RAM usage sits around 160 MB. 

There are five types of tasks that can be configured:
* **Port Forward** - opens a port over ssh, setting the local and remote ports. This task type also has the 
  ability to kill a local process if it is preventing the local port from being opened. 
* **Long Job** - This is a long-running job that needs occasional status checks or restarts
* **Cookie** - a one-off job designed to grab a cookie for copy-paste into Postman
* **Monitor** - watch a process using its PID
* **Docker Monitor** - periodically checks to see if a Docker container goes down

Currently, configuration is done using a JSON file sored at `$HOME/forrest_config.json`, which is pre-populated with 
some default values at first launch. If there is an issue with the configuration, simply delete or rename this file and 
it will be re-created the next time the program runs.

Note that configuration changes currently require the program to be closed, then re-opened to take effect.

Features/TODO
-------------
- [x] implement the Port Forward task type
- [x] implement the Long Job task type
- [ ] implement the Cookie task type
- [ ] implement the Monitor task type
- [ ] implement the Docker Monitor task type
- [x] implement the running of tasks on the local computer
- [x] implement the running of tasks on a remote host over SSH
- [x] start and stop tasks using the GUI
- [x] implement viewing of task logs using the GUI
- [ ] have the log view update automatically --> bugged, see https://github.com/JetBrains/compose-jb/issues/733
- [ ] add the ability to add tasks using the GUI
- [ ] add the ability to remove tasks using the GUI
- [ ] add the ability to edit tasks using the GUI
- [x] implement import and export of task configurations
- [ ] import and export configurations using the GUI
- [ ] configure one or more remote machines as targets using the GUI


Development
-----------
This project was created using [JetBrains Compose for Desktop](https://www.jetbrains.com/lp/compose/).

Tinkering can be done using IntelliJ and AdoptOpenJDK runtime version 15. Please note that Compose for Desktop is 
still in alpha, so the version and vendor of the Java SDK appears to have some impact when packaging, so use 
AdoptOpenJDK when possible.
