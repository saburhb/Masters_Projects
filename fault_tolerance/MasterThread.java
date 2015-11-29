import java.io.*;
import java.net.*;


class MasterThread implements Runnable{

	int port;
	int server_id;
	ServerSocket servsock = null;
	Socket consock = null;
	
	
		
	MasterThread(int port_no, int serv_id)
	{
		this.port = port_no;
		this.server_id = serv_id;
	}
	
	
	@Override
	public void run() {
		
		try
		{
			servsock  = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}	
		
		
		while(true)
		{
			try
			{
				consock = servsock.accept();
				System.out.println("Connection socket is created");
				
				MasterChildThread st = new MasterChildThread(consock, server_id);
				Thread ts = new Thread(st);
				ts.start();						
			}
			catch(IOException e)
			{
				//e.printStackTrace();
				continue; // test
			} 
			
		}	
		
	}

}


/****************************************************************/
class MasterChildThread implements Runnable {
	
	Socket child;
	int server_id;
	int fail_id = 0;
	String hostAddr = "";
	ObjectInputStream  oic = null;
	ObjectOutputStream  ooc = null;
	Socket sock = null;
	int srv_id = 0;
	
	
	Socket[] sock1 = new Socket[6];
	ObjectOutputStream[]  oos = new ObjectOutputStream[6];
	
	MasterChildThread(Socket consock, int serv_id)
	{
		this.child = consock;
		this.server_id = serv_id;
					
		try 
		{
	       	oic = new ObjectInputStream(child.getInputStream());			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}
	
	public void run() {
				
		System.out.println("Remote host is now connected to Master " );
		
		try
		{
			while(true)
			{	
				Message message = (Message)(oic.readObject());
				
				if(message == null )
				{					
					continue;
				} 
				else
				{
					fail_id = message.sourceId;
					
					if(message.msg_type.equals("NOTIFICATION"))
					{
						if(message.newhead != 0)
						{
							if(Master.first_not == 0)
							{
								System.out.println("Notify NEW HEAD to all other servers except crashed server #" +message.crashed_server_id);
								Message m1 = new Message();
								m1.msg_type = "NOTIFICATION";
								m1.newhead = message.newhead;
								m1.time_stamp[message.head_server] = message.time_stamp[message.head_server];
								m1.msg_dest = "SERVER";
								m1.crashed_server_id = message.crashed_server_id;
								m1.msg_source = "SERVER";
								m1.sourceId = 0;
								
								notifyFail(message.crashed_server_id, m1);							
								Master.first_not = 1;
							}
						}
					}
					else if(message.msg_type.equals("UNBLOCK"))
					{
						++(Master.count_unblock);
						System.out.println(" Number of UNBLOCK Request received = " + Master.count_unblock);
						
						if(message.crashed_server_id != 5)
						{
							if(Master.count_unblock == 3)
							{
								message.msg_source = "SERVER";
								message.sourceId = 0;
								message.msg_type = "FINAL";							
								
								System.out.println("Sending FINAL message to unblock all the Servers except crashed id: " + message.crashed_server_id);
								notifyFail(message.crashed_server_id, message);							
							}
						}
						else
						{
							if(Master.count_unblock == 4)
							{
								message.msg_source = "SERVER";
								message.sourceId = 0;
								message.msg_type = "FINAL";							
								
								System.out.println("Sending FINAL message to unblock all the Servers except crashed id: " + message.crashed_server_id);
								notifyFail(message.crashed_server_id, message);							
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{	
			Master.failed_server = fail_id;
			if(Master.first_excep == 0)
			{
				System.out.println("Child Thread got exception");
				System.out.println("server #" +fail_id+ " has died");			
					
				Message m = new Message();
				m.msg_type = "NOTIFICATION";
				m.msg_source = "SERVER";
				m.sourceId = 0;
				m.crashed_server_id = Master.failed_server;
				
				System.out.println("Server #" + Master.failed_server + " has crashed : Notify to other servers");
				notifyFail(Master.failed_server, m);														
				//System.exit(0);
				notifyClient(Master.failed_server, m);
				Master.first_excep = 1;
			}
		} 
			
			
	} // end of run
	
	
	
	/***************************************************************/
	public void notifyFail(int server_id, Message m)
	{
		String[] ips = new String[5];
		int[] ports = new int[5];
		int max;
		
		if(m.newhead == 0)
		{
			max = 5;
		}
		else
		{
			max = 4;
		}
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
					sock1[k] = new Socket(ip, port);
					
					oos[k] = new ObjectOutputStream(sock1[k].getOutputStream());
					
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
		
		for(int j=1; j<=max; j++)
		{	
											
			if( (j != server_id))
			{
				try 
				{
					oos[j].writeObject(m);
					oos[j].flush();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	/*********************************************************/
	
	
	/***************************************************************/
	public void notifyClient(int server_id, Message m)
	{
		String[] ips = new String[3];
		int[] ports = new int[3];
				
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader("config_cli.txt"));
			String info = "";			
			
			for (int i=0; i < 6; i++)
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
			
			for(int k=1; k<=3; k++)
			{	
				String ip = ips[k-1];
				int port = ports[k-1];
					
				System.out.println("Create socket sock[" +k+ "] to conncet to ip: " +ip+ " port: " +port);
				sock1[k] = new Socket(ip, port);					
				oos[k] = new ObjectOutputStream(sock1[k].getOutputStream());			
				
				oos[k].writeObject(m);
				oos[k].flush();
				
			}			
			
		} 
		catch (Exception e) 
		{
			//do nothing
		} 
				
		
	}
	
	
	/*********************************************************/
		
}



