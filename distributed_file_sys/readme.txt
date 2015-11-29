
Name: Sabur Hassan Baidya
UTD ID: 2021100302 



PROJECT DESCRIPTION : To implement a protocol for object replication with no server failure,
--------------------  .



FILES:
----------------------------------------
Code Files
------------
 Client.java, ClientThread.java, ReadClient.java, ReadClientThread.java, Server.java, ServerChildThread.java, ServerConnectThread.java, ServerThread.java, TailThread.java

config files
-------------
config_cli.txt, config_serv.txt (containing ports and IPs of the clients and servers in alternate lines)

Data file(s)
-------------
file1.txt ( Should be created in the Server folders)



COMPILING INSTRUCTIONS:
--------------------------------

***** for Client ******
----------------------
javac Client.java
javac ReadClient.java

***** for Server *******
------------------------
javac Server.java





EXECUTING INSTRUCTIONS:
---------------------

For Client
--------------
1) put the config.txt file in Client directory
2) java Client <CLIENT_ID> <CLIENT_PORT>

For ReadClient
----------------
2) java Client <CLIENT_ID> <CLIENT_PORT> 

For Server:
-------------
1) Create the files to be READ/WRITTEN in a directory (The contents of the file is same in all servers)
2) java Server <SERVER_ID> <SERVER_PORT> 




RESULTS:
---------

Client Output
-----------------

...............
...............

*********** Generating  Request Message ************
 Message Type: REQUEST
 Request for: WRITE
 Request from Client: 2
 Request to Server: 4
 Operation Request on File : file1.txt
 Connect to Server at 10.176.67.97 on port 8880
 Request sent to server
*** ACK Received from Server ***
 @@@@@@@@@ ACK: SUCCESS@@@@@@@@@
*** Setting ACK flag to 1 ***
*********** Generating  Request Message ************
 Message Type: REQUEST
 Request for: READ
 Request from Client: 2
 Request to Server: 3
 Operation Request on File : file1.txt
 Connect to Server at 10.176.67.96 on port 7770
 Request sent to server
*** ACK Received from Server ***
 @@@@@@@@@ ACK: SERVER ID: <3> --- CLIENT ID: 2 --- HEAD SERVER: 4 ---SEQUENCE NUMBER: 6 --- at LOCAL TIME: 23@@@@@@@@@
*** Setting ACK flag to 1 ***
*********** Generating  Request Message ************

...........
............


ReadClient Output
-----------------
 .................
 .................
 *********** Generating  Request Message ************
  Request sent to server1
  Request sent to server2
  Request sent to server3
  Request sent to server4
  Request sent to server5
 *** ACK Received from Server ***
  @@@@@@@@@ ACK: SERVER ID: <5> --- CLIENT ID: 2 --- HEAD SERVER: 5 ---SEQUENCE NUMBER: 2 --- at LOCAL TIME: 12@@@@@@@@@
 *** ACK Received from Server ***
  @@@@@@@@@ ACK: SERVER ID: <5> --- CLIENT ID: 2 --- HEAD SERVER: 5 ---SEQUENCE NUMBER: 2 --- at LOCAL TIME: 12@@@@@@@@@
 *** ACK Received from Server ***
  @@@@@@@@@ ACK: SERVER ID: <5> --- CLIENT ID: 2 --- HEAD SERVER: 5 ---SEQUENCE NUMBER: 2 --- at LOCAL TIME: 12@@@@@@@@@
 *** ACK Received from Server ***
  @@@@@@@@@ ACK: SERVER ID: <5> --- CLIENT ID: 2 --- HEAD SERVER: 5 ---SEQUENCE NUMBER: 2 --- at LOCAL TIME: 12@@@@@@@@@
 *** ACK Received from Server ***
  @@@@@@@@@ ACK: SERVER ID: <5> --- CLIENT ID: 2 --- HEAD SERVER: 5 ---SEQUENCE NUMBER: 2 --- at LOCAL TIME: 12@@@@@@@@@
 *** Setting ACK flag to 1 ***
 *********** Generating  Request Message ************
.................
.................



Server Side Output:
-----------------------------------------------------------------------

{net33:~/new/AOS/project2} java Server 3 7777
Connection socket is created
Connection socket is created
Create socket sock[1] to conncet to ip: 10.176.67.94 port: 5555
Create socket sock[2] to conncet to ip: 10.176.67.95 port: 6666
Create socket sock[4] to conncet to ip: 10.176.67.97 port: 8888
Create socket sock[5] to conncet to ip: 10.176.67.98 port: 9999
Connection socket is created
Connection socket is created
Connection socket is created
 Message Sequence Number: 0
 Message Type: REQUEST
 Request for: WRITE
 Request from CLIENT1
 Operation Request on File : file1.txt
This is the head server for message #1from server #3
 Add in local history and send to other servers except TAIL
******* Message from JOB QUEUE *******
Head Server : 3
 Sequence number : 1
Message Destination :SERVER
 Message Sequence Number: 1
 Message Type: COMMIT
 Request from SERVER4
 Operation Request on File : file1.txt
******** WRITING IN FILE *******
 Content to be written in file file1.txt : SERVER ID: 3 --- writes message with                                                                                         SEQUENCE NUMBER : 1  --- given by HEAD SERVER : 3 --- at LOCAL TIME: 1
Enque the message to send ack to client #1
Enque the message to send ack to client #1
******* Message from JOB QUEUE *******
Head Server : 3
 Sequence number : 1
Message Destination :SERVER
******* Message from JOB QUEUE *******
Head Server : 3
 Sequence number : 1
Message Destination :CLIENT
Sending ACK to client 1

...........
............



