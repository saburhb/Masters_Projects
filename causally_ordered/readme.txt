
Name: Sabur Hassan Baidya
UTD ID: 2021100302 



PROJECT DESCRIPTION : TO implement a protocol for object replication with no server failure,
--------------------  .



FILES:
----------------------------------------
Code Files
------------
 Client.java, ClientPartThread.java, ServerPartThread.java, MutualExclusion.java, Server.java

config files
-------------
config.txt(containing ports and IPs of the clients in alternate lines)

Data files
---------------
file1.txt, file2.txt, file3.txt ( Should be created in the Server folders)



COMPILING INSTRUCTIONS:
--------------------------------

***** for Client******
----------------------
javac Client.java


*****for Server*******
----------------------
javac Server.java





EXECUTING INSTRUCTIONS:
---------------------

For Client
--------------
1) put the config.txt file in Client directory
2) java Client <CLIENT_PORT> <CLIENT_ID>


For Server:
-------------
1) Create the files to be READ/WRTTEN in a directory (The contents of the file is same in all servers)
2) java Server <SERVER_PORT> <SERVER_ID> <ABSOLUTE PATH OF THE FILES   --- e.g. /home/004/s/sx/sxb103820/new/aos_project/S2>




RESULTS:
---------

Client Side
-----------------

Connect to other Clients
Create client socket sock[1] to conncet to ip: 10.176.67.99 port: 9999
Create client socket sock[2] to conncet to ip: 10.176.67.99 port: 5555
Create client socket sock[3] to conncet to ip: 10.176.67.99 port: 6666
Create client socket sock[4] to conncet to ip: 10.176.67.99 port: 7777
Server socket of the client is created
Server socket of the client is created
read object from connection socket
Server socket of the client is created
read object from connection socket
read object from connection socket
Server socket of the client is created
read object from connection socket
*********** Generating  Request Message ************
 Random generated Index = 1
 Message Id: 1
 Message Timestamp: 1
 Request from Client: 5
 Message type: request
 Message Conent: Client C5--> Write the message : 1 *** Request sent at time : 1
*********** Generating  Request Message ************
 Random generated Index = 0
 Message Id: 2
 Message Timestamp: 2
 Request from Client: 5
 Message type: request
 Message Conent: null

.....
.....
*********** MESSAGE RECEIVED FROM OTHER CLIENTS ***********
 Message Id: 4
 Message Timestamp: 4
 Request from Client: 4
 Message type: request
 Message Conent: null
Sending reply message for reuest from Client4for msg id: 4
********* TEST 2 ***********
read object from connection socket
************ FILES IN TEH SERVER 1**********
file1.txt,file2.txt,file3.txt,Server.class,ServerThread.class,Message.class,
************ FILES IN TEH SERVER 2**********
file1.txt,file2.txt,file3.txt,Server.class,ServerThread.class,Message.class,
************ FILES IN TEH SERVER 3**********
file1.txt,file2.txt,file3.txt,Server.class,ServerThread.class,Message.class,

.............
.............
Client5can enter the critical section for file 1
*************** MESSAGE RECEIVED FROM SERVER *******************
 Message Id: 3
 Request from Client: 5
 Request processed by Server: 1
 Message Conent: Client C5--> Write the message : 3 *** Request sent at time : 3
*************** MESSAGE RECEIVED FROM SERVER *******************
 Message Id: 3
 Request from Client: 5
 Request processed by Server: 2
 Message Conent: Client C5--> Write the message : 3 *** Request sent at time : 3
*************** MESSAGE RECEIVED FROM SERVER *******************
 Message Id: 3
 Request from Client: 5
 Request processed by Server: 3
 Message Conent: Client C5--> Write the message : 3 *** Request sent at time : 3

..........
.............




Server Side Output:
-----------------------------------------------------------------------

read object received from the client
read object received from the client
read object received from the client
Message received from client: 4  to WRITE file file2.txt
read object received from the client
Message received from client: 3  to WRITE file file1.txt
read object received from the client
Message received from client: 4  to READ file1.txt
*************** Content Read from the File ****************
Client C3--> Write the message : 3 *** Request sent at time : 3
Message received from client: 1  to WRITE file file2.txt
...........
............



