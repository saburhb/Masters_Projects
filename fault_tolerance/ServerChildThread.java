import java.io.*;
import java.net.Socket;
//import java.util.Iterator;


class ServerChildThread implements Runnable{

	Socket child;
	int server_id;
	int client_id;
	ObjectInputStream  oic = null;
	String local_ip = "";
	
	ServerChildThread(Socket consock, int serv_id)
	{
		this.child = consock;
		this.server_id = serv_id;
					
		try 
		{
			oic = new ObjectInputStream(child.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void run() {
		
		while(true)
		{
			try 
			{				
				//System.out.println("read object from connection socket");
				
				Message message = (Message)(oic.readObject());
				
				synchronized(message)
				{
					System.out.println(" Request from " +message.msg_source + message.sourceId);
					if(message.msg_source.equals("SERVER"))
					{
						System.out.println(" Message Sequence Number: " + message.message_id);
					}
					System.out.println(" Message Type: " + message.msg_type);
					if(message.msg_type.equals("REQUEST"))
					{
						System.out.println(" Request for: " + message.request);
						System.out.println(" Operation Request on File : " +message.file_name);
					}
										
					
					
					Message mq = new Message();
					Message ml = new Message();
					ml.msg_source = message.msg_source;
					ml.sourceId = message.sourceId;
					ml.msg_type = message.msg_type;
					ml.file_name = message.file_name;
					
						
					
					if(server_id != Server.TAIL_ID)
					{
						if(message.msg_type.equals("FINAL"))
						{	
							
							System.out.println(" Unblocking the sending of Request messages by setting SUSPEND = 0");
							Server.SUSPEND = 0;
							
							if(Server.crashed_id != 5)
							{
								//check if it is the newly elected head , if not delete all the history
								if(server_id == Server.newHead)
								{
									System.out.println(" This server is the newHead: regenarate all history");
									//replace the head id of crashed server if any, by its own id
									//regenerate all the messages in history by updating the vector clock by its own clock value and all other 0s 
									//and forward for remaining node in jq
									if(Server.al.size() > 0)
									{
										for(int i=0; i<Server.al.size(); i++)
										{
											Server.al.get(i).head_server = server_id;
											Server.al.get(i).message_id = ++(Server.seq_num);
											
											for(int j=1; j<5; j++)
											{
												if(j == server_id)
												{
													Server.al.get(i).time_stamp[j] = ++(Server.clock);
												}
												else
												{
													Server.al.get(i).time_stamp[j] = 0;
												}
											}											
										}
										
										//forward the newly generated request
										if(Server.al.size() > 0)
										{
											for(int i=0; i<Server.al.size(); i++)
											{
												Message me = new Message();
												
												me.head_server = server_id ;
												me.message_id = ++(Server.seq_num);
												me.time_stamp[server_id] = Server.al.get(i).time_stamp[server_id];
												me.msg_source = "SERVER";
												me.sourceId = server_id;
												me.msg_dest = "SERVER";
												me.destId = 0;
												me.msg_type = Server.al.get(i).msg_type;
												me.request = Server.al.get(i).request;
												me.file_name = Server.al.get(i).file_name;
												me.ack = "";
												me.clientId = Server.al.get(i).clientId;
												
												Server.jq.add(me);
												
												System.out.println("Sending regenerated request to jq");
											}
										}
									}
									else
									{
										System.out.println("There is no history as of now");
									}									
								}
							}
							else if(Server.crashed_id == 5)
							{								
								Server.TAIL_ID = Server.TAIL_ID - 1;
								if(server_id == Server.TAIL_ID)
								{
									try
									{
										System.out.println("New TAIL ID : "+ Server.TAIL_ID);
										//run the tail thread
										TailThread st1 = new TailThread(server_id);
										Thread t1 = new Thread(st1);
										t1.start();
										break;
									}
									catch(Exception e)
									{
										
									}
								}
								
								if(Server.al.size() > 0)
								{
									for(int i=0; i<Server.al.size(); i++)
									{
										if(Server.al.get(i).head_server == server_id)
										{
											Message me = new Message();
											
											me.head_server = server_id ;
											me.message_id = Server.al.get(i).message_id;
											me.time_stamp[server_id] = ++(Server.clock);
											me.msg_source = "SERVER";
											me.sourceId = server_id;
											me.msg_dest = "SERVER";
											me.destId = 0;
											me.msg_type = Server.al.get(i).msg_type;
											me.request = Server.al.get(i).request;
											me.file_name = Server.al.get(i).file_name;
											me.ack = "";
											me.clientId = Server.al.get(i).clientId;
											
											Server.jq.add(me);
											
											System.out.println("Sending regenerated request to jq");
										}
									}
								}
								else
								{
									System.out.println("There is no history as of now");
								}
								
							}
														
						}						
						else if(message.msg_type.equals("NOTIFICATION"))
						{
							if(message.newhead == 0)
							{
								System.out.println("The Server " + message.crashed_server_id + " has crashed");
								Server.crashed_id = message.crashed_server_id;
								
								Server.notification = 1;
								Server.SUSPEND = 1;
								
								local_ip = child.getLocalAddress().toString();
								try
								{								
									WaitingThread ts = new WaitingThread(local_ip, server_id);
									Thread t = new Thread(ts);
									t.start();			
								}		 
								catch (Exception e) 
								{					
									e.printStackTrace();
									System.exit(0);
									
								}						
								//break; // testing only								 
								 
							}
							else
							{
								Server.newHead = message.newhead;
								//it got notification from some other servers for new head
								if( (message.newhead != 0) && (Server.wait_flag1 == 0) )
								{
									System.out.println("New head replacing the crashed head is server #" + message.newhead);
									Server.wait_flag1 = 1;
									
								}
							}
						}//end of NOTIFICATION
						else if(message.msg_type.equals("REQUEST"))
						{								
							if(message.request.equals("WRITE"))
							{
								if(message.msg_source.equals("CLIENT"))
								{
									
									//copy to a different message
									
									/* Add a sequence number and its own timestamp to the message and send to all servers except tail 
									 * also write in local history in an arraylist */ 
									mq.head_server = server_id ;
									mq.message_id = ++(Server.seq_num);
									mq.time_stamp[server_id] = ++(Server.clock);
									mq.msg_source = "SERVER";
									mq.sourceId = server_id;
									mq.msg_dest = "SERVER";
									mq.destId = 0;
									mq.msg_type = message.msg_type;
									mq.request = message.request;
									mq.file_name = message.file_name;
									mq.ack = "";
									mq.clientId = message.clientId;
																		
									System.out.println("This is the head server for message #" +mq.message_id + "from server #" +mq.head_server );
									System.out.println(" Add in local history and send to other servers except TAIL");
									
									
									ml.message_id = mq.message_id;
									ml.head_server = mq.head_server;
									ml.msg_type = message.msg_type;
									ml.request = message.request;
									ml.file_name = message.file_name;
									ml.clientId = message.clientId;
									
									Server.jq.add(mq);
									Server.al.add(ml); /* adding pending request in local history */
									
								}
								else if(message.msg_source.equals("SERVER"))
								{
									if(message.sourceId != Server.TAIL_ID) /*TAIL ID*/
									{
										/* Writing message to be sent to TAIL */
										mq.head_server = message.head_server;
										mq.message_id = message.message_id;
										mq.msg_source = "SERVER";
										mq.sourceId = server_id;
										mq.msg_dest = "SERVER";
										mq.destId = Server.TAIL_ID;
										mq.time_stamp[server_id] = ++(Server.clock);
										mq.msg_type = message.msg_type;
										mq.request = message.request;
										mq.file_name = message.file_name;
										mq.ack = "";
										mq.clientId = message.clientId;
										
										/* Writing message for local history */
										ml.message_id = message.message_id;
										ml.head_server = message.head_server;
										ml.msg_type = message.msg_type;
										ml.request = message.request;
										ml.file_name = message.file_name;
										ml.clientId = message.clientId;																												
																				
										System.out.println("This is an intermediate server for message #" +message.message_id);
										System.out.println(" Add in local history and send to TAIL");
										
										Server.jq.add(mq);
										Server.al.add(ml);							
									}
									else
									{
										//message received from TAIL
										// No REQUEST should come from TAIL
										
									}
								}
							}
							else if(message.request.equals("READ"))
							{
								// check Arraylist if there is pending request for this file
								// if not, open the file and read and send the msg for client
								//else forward to tail
								
								int client = message.sourceId;
								int pending = 0;
								
								if(Server.al.size() > 0)
								{
									for(int k=0; k< Server.al.size(); k++)
									{
										if(message.file_name.equals(Server.al.get(k).file_name))
										{
											pending = 1;
											break;
										}
									}
								}
								
								if(pending == 0)
								{
									// Read from local file
									FileOperation.fileRead(message, server_id);
									
									//Construct a message to be sent as ACK to Client
									Message mc = new Message();
									mc.msg_type = "ACK";
									mc.msg_source = "SERVER";
									mc.sourceId = server_id;
									mc.msg_dest = "CLIENT";
									mc.destId = client; // saved earlier
									mc.ack = message.ack;
									mc.file_name = message.file_name;
									mc.request = "";
									
									Server.jq.add(mc);
									
								}
								else
								{
									//construct a read message to be sent to TAIL
									mq.msg_source = "CLIENT";
									mq.sourceId = client;
									mq.msg_dest = "SERVER";
									mq.destId = Server.TAIL_ID;
									mq.msg_type = message.msg_type;
									mq.request = message.request;
									mq.file_name = message.file_name;
									mq.ack = "";
									mq.clientId = message.clientId;
									
									Server.jq.add(mq);
								}
								
							}
						}//end of REQUEST
						else if(message.msg_type.equals("COMMIT"))
						{	
							
							// open the file and write in the file
							FileOperation.fileWrite(message, server_id, Server.clock);
							String ack_m = message.ack;
							int m_id = message.message_id;
							int head = message.head_server;
							
											
							if(server_id > 1)
							{
								if((server_id == 2) && (Server.crashed_id == 1))
								{
									//do not forward
								}
								else
								{
									mq.head_server = message.head_server;
									mq.message_id = message.message_id;
									mq.msg_source = "SERVER";
									mq.sourceId = server_id;
									mq.msg_dest = "SERVER";
									if(Server.crashed_id == (server_id-1))
									{
										mq.destId = (server_id -2);
									}
									else
									{
										mq.destId = (server_id -1);
									}
									mq.unique_id = message.unique_id;
									mq.msg_type = message.msg_type;
									mq.request = message.request;
									mq.file_name = message.file_name;
									mq.ack = ack_m;
									mq.clientId = message.clientId;
									for(int c=0; c<=5; c++)
									{
										mq.time_stamp[c] = message.time_stamp[c];
									}
									
									Server.jq.add(mq);
								}
							}
								
							
							if(head == server_id)
							{
								if(Server.al.size() > 0)
								{
									client_id = message.clientId;																				
								}
								
								System.out.println("Enque the message to send ack to client #" +client_id);
								//send ACK to client
								Message m = new Message();
								//check the arraylist corresponding msg_id, to find the sourceId which is the clientId
								m.msg_type = "ACK";
								m.msg_source = "SERVER";
								m.sourceId = server_id;
								m.msg_dest = "CLIENT";
								m.destId = client_id; // obtained above
								m.ack = ack_m;
								m.message_id = m_id;
								m.head_server = head;
								m.ack = message.ack;
								m.file_name = message.file_name;
								m.request = "";
								
								
								System.out.println("Enque the message to send ack to client #" +client_id);
								Server.jq.add(m);
							}
							
							// delete the corresponding messsage_id from the array list
							if(Server.al.size() > 0)
							{
								for(int i=0; i<Server.al.size(); i++)
								{
									if((Server.al.get(i).message_id == message.message_id) && (message.head_server == Server.al.get(i).head_server))
									{										
										Server.al.remove(i); // removing the history for the message id										
									}
								}
							}
						}
					}//End of server is not TAIL
					else
					{ 
						if((message.msg_type.equals("NOTIFICATION")) && (message.crashed_server_id != 0) )
						{
							Server.crashed_id = message.crashed_server_id;
						}
						
						if(message.msg_source.equals("CLIENT"))
						{
							if(message.request.equals("READ"))
							{
								FileOperation.fileRead(message, Server.TAIL_ID);
								
								Message reply_msg = new Message();
								reply_msg.msg_type = "ACK";
								reply_msg.msg_source = "SERVER";
								reply_msg.sourceId = Server.TAIL_ID;
								reply_msg.msg_dest = "CLIENT";
								reply_msg.destId = message.sourceId;
								reply_msg.file_name = message.file_name;
								reply_msg.request = message.request;
								reply_msg.ack = message.ack;
								
								Server.jq.add(reply_msg);
							}
							else if(message.request.equals("WRITE"))
							{
								Message m1 = new Message();
								m1.message_id = ++(Server.seq_num);
								m1.ack = message.ack;
								m1.msg_type = "COMMIT";
								m1.request = message.request;
								m1.head_server = Server.TAIL_ID;
								m1.file_name = message.file_name;
								m1.msg_source = "SERVER";
								m1.msg_dest = "SERVER";		
								m1.clientId = message.clientId;
								int time = ++(Server.clock);
								for(int i=0; i<=5; i++)
								{
									m1.time_stamp[i] = time; 
								}
								
								Server.tailq.add(m1);
								
								Message reply_msg = new Message();
								reply_msg.msg_type = "ACK";
								reply_msg.msg_source = "SERVER";
								reply_msg.sourceId = Server.TAIL_ID;
								reply_msg.msg_dest = "CLIENT";
								reply_msg.destId = message.sourceId;
								reply_msg.file_name = message.file_name;
								reply_msg.request = message.request;
								reply_msg.ack = "SUCCESS";
								
								Server.jq.add(reply_msg);
							}
						}
						else
						{
						//This server is TAIL and getting request from other Servers
							int msgId = message.message_id;
							int head = message.head_server;
							int count = 0;
							int index = 0;
							int max = 0;
							
							System.out.println(" Crashed Server ID : " + Server.crashed_id);
							if(Server.crashed_id != 0)
							{
								max = 2;
							}
							else
							{
								max = 3;
							}
							
							Server.ml.add(message);
																				
							if(Server.ml.size() > 0)
							{
								for(int i=0; i<Server.ml.size(); i++)
								{
									if((msgId == Server.ml.get(i).message_id) && (head == Server.ml.get(i).head_server))
									{
										count++;
									}
								}
								
								
							}
														
							
							if(count == max)
							{
								Message m = new Message();
								m.message_id = msgId;
								m.ack = message.ack;
								m.msg_type = "COMMIT";
								m.request = message.request;
								m.head_server = head;
								m.file_name = message.file_name;
								m.msg_source = "SERVER";
								m.msg_dest = "SERVER";
								m.clientId = message.clientId;
								m.time_stamp[5] = ++(Server.clock);
								for(int i=0; i<Server.ml.size(); i++)
								{
									if((msgId == Server.ml.get(i).message_id) && (head == Server.ml.get(i).head_server))
									{
										for(int l=0; l<=4; l++)
										{
											if(Server.ml.get(i).time_stamp[l] > 0)
											{
												m.time_stamp[l] = Server.ml.get(i).time_stamp[l];
											}
										}
										
										if(message.crashed_server_id != 0)
										{
											m.time_stamp[message.crashed_server_id] = 0;
										}
									}
								}
								
								synchronized(Server.ml)
								{
									for(int j=0; j<Server.ml.size(); j++)
									{
										if((msgId == Server.ml.get(j).message_id) && (message.head_server == Server.ml.get(j).head_server))
										{
											index = Server.ml.get(j).sourceId;
											m.time_stamp[index] = Server.ml.get(j).time_stamp[index];
											Server.ml.remove(j); // deleting the entries corresponding to msg_id when a such messages are there
										}
									}
								}
								
								System.out.println(" Preparing vector and putting in tailq");
								Server.tailq.add(m);	
							}
						}
					}// End of TAIL activity
				} //end of Synchronized					
								
			} 
			catch(EOFException e)
			{
//				e.printStackTrace();
				/*try 
				{
					oic.close();
				} 
				catch (IOException e1) 
				{					
					e1.printStackTrace();
				} */
				continue;
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e1) 
			{
				e1.printStackTrace();
			}
		
		}
				
	}
	
}
