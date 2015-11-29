import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class ServerConnectThread implements Runnable{

	int server_id;
	Socket[] sock = new Socket[6];
	ObjectOutputStream[]  oos = new ObjectOutputStream[6];
	
	ServerConnectThread(int srv_id)
	{
		this.server_id = srv_id;
	}
	
	public void run() {
		
		String[] ips = new String[5];
		int[] ports = new int[5];
		
		
		/* (1) Connect to other servers */
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader("config_serv.txt"));
			String info = "";			
			
			for (int i=0; i < 10; i++)
			{
				try 
			    {
					info = br.readLine();
			    } 
			    catch (IOException e) 
			    {					
			    	e.printStackTrace();
				}
			    if( i%2 == 0)
			    {			    	 
			    	ports[i/2] = Integer.valueOf(info).intValue();
			    }
			    else
			    {
			    	ips[i/2] = info;
			    }
			}

			try 
			{
				br.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			for(int k=1; k<=5; k++)
			{						
				if( k != server_id)
				{
					String ip = ips[k-1];
					int port = ports[k-1];
					
					System.out.println("Create socket sock[" +k+ "] to conncet to ip: " +ip+ " port: " +port);
					sock[k] = new Socket(ip, port);
					
					oos[k] = new ObjectOutputStream(sock[k].getOutputStream());
					
				}
			}			
			
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		
		
		/* (2) Process the job queue */
		while(true)
		{
			
			if(Server.jq.size() > 0)
			{
				if(Server.jq.peek() == null)
				{
					System.out.println(" @@@@@@@ Message is null @@@@@@@");
				}
				Message m = (Message)Server.jq.peek();
				
				System.out.println("******* Message from JOB QUEUE *******");
				
				System.out.println("Head Server : " + m.head_server);
				System.out.println(" Sequence number : " +m.message_id);
				if(!(m.msg_dest.equals(null)))
				{
					System.out.println("Message Destination :" + m.msg_dest);
				}
				else
				{
					System.out.println("Message Destination is Null");
				}
				System.out.println(" Message Type : " +m.msg_type);
				
				if(m.msg_dest.equals("SERVER"))
				{
					if((m.msg_type.equals("REQUEST")) && (Server.SUSPEND == 1))
					{
						System.out.println("Message forwarding is blocked for some time ");
						Server.jq.poll();
						
					}
					else
					{
						System.out.println("size of jq = " + Server.jq.size());
						Message ms = (Message)Server.jq.poll();
						if(ms == null)
						{
							System.out.println("Object in jq is null for server");
						}
						
						if(ms.destId == 0)
						{
							//Send to other 3 servers 
							try 
							{							
								for(int j=1; j<Server.TAIL_ID; j++)
								{						
									if( !((j == server_id) || (j == Server.crashed_id)))
									{										
										oos[j].writeObject(ms);
										oos[j].flush();										
									}
								}							
							} 
							catch (IOException e) 
							{
								continue;
							}
						}
						else
						{
							try
							{
								//send to the corresponding predecessor for COMMIT or to TAIL for request
								
								oos[ms.destId].writeObject(ms);
								oos[ms.destId].flush();								
							}
							catch(IOException e)
							{
								continue;
							}						
						}
					}
					
				} // if msg_type SERVER ends
								
				else if(m.msg_dest.equals("CLIENT"))
				{
					Message ms = (Message)Server.jq.poll();
					if(ms != null)
					{
						System.out.println("Object in jq is null");
					}
					
					//send to the client // for that establish a connection to client, so the client should have a server part as well
					System.out.println("Sending ACK to client " +ms.destId);
					int port_client = 0;
					String ip_client = "";
					ObjectOutputStream  ooc = null;
					
					try 
					{
						BufferedReader br = new BufferedReader(new FileReader("config_cli.txt"));
						String s = "";
												
						for(int k=0; k<6; k++)
						{
							try 
						    {
								s = br.readLine();
						    } 
						    catch (IOException e) 
						    {					
						    	e.printStackTrace();
							}
							
							if((m.destId - 1)*2 == k)
							{
								port_client = Integer.valueOf(s).intValue();
							}
							if((m.destId*2 -1) == k)
							{
								ip_client = s;
							}
														
						}
						
						try 
						{
							br.close();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
						
						//create connection to the client
						if((port_client != 0) && !(ip_client.equals("")))
						{
							Socket sock_client = null;
							
							System.out.println("Creating connction to Client @ IP: "+ip_client + "and Port: " +port_client);
							sock_client = new Socket(ip_client, port_client);							
							ooc = new ObjectOutputStream(sock_client.getOutputStream());
							
							
							ooc.writeObject(ms);
							ooc.flush();
							
							//ooc.close(); 
							//sock_client.close();
						}
					}
					catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					}  
					catch (IOException e) 
					{
						e.printStackTrace();
						continue; //test
					}
				}				
			
			} // while ends on jq			

		} // while true ends	
		
		
	}
	
		

}
