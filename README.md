RemBox allows you to access the hard drive of another PC running this program.

How to use:

(Client)
- Comment out "new Server()" in the main class
- Change the URL in IPGrabber.java to an URL where you want to store your server's IP as plaintext. Example: https://pastebin.com/raw/abcd0123
- Compile as .jar and run on the client PC

(Server)
- Comment out "new Client()" in the main class and un-comment "new Server()"
- Run the app and enter a port into the bar at the top of the UI
- Put the IP of the machine your server is running on as plaintext in the first line of the website that
  you put in IPGrabber.java, then a : and the port you specified. Example: Edit the pastebin entry on https://pastebin.com/abcd0123 and write 127.0.0.1:32767.
  Note: IPv6 IPs were not tested and probably don't work.
  
(Server UI)
- When a client connects, it will show up in the main UI.
- Right click a client to access it.
  
Features:
- Manage Client UI currently does nothing.
- File Explorer grants you access to the client's file system:
    - Edit remote files on your PC
    - Create new files and folders on the remote system
    - Download files from the remote system
    - Upload your own files to the remote system
    - Remotely run any file located on the remote system
    - Rename and move files on the remote system
    - Delete any file on the remote system
    - See basic properties of files on the remote system
    
  The file manager or some of its features may not work if the clientside app lacks required access permission.
  
Note that this app will only work if the Java installations on all systems are given internet access. An active firewall can stop the app from connecting.
