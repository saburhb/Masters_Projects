import java.io.*;
import java.util.*;
import java.net.*;


class ClientPartThread implements Runnable{

	HashMap<Integer, NetDetails> list_clients = new HashMap<Integer, NetDetails>();
	Socket[] sock = new Socket[6];
	
	ObjectOutputStream[]  oos = new ObjectOutputStream[6];
	//ObjectInputStream[]  ois = new ObjectInputStream[4];
	
	
	int port;
	int client_id;
	
	
	//constructor
	ClientPartThread(int port_number, int cli_Id)
	{
		this.port = port_number;
		this.client_id = cli_Id;
		
		
		
	}

	@Override
	public void run() {
		String[] ips = new String[5];
		int[] ports = new int[5];
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("config.txt"));
			String info = "";			
			
			for (int i = 0; i < 10; i++)
			{
			     try {
					info = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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

			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int k=1; k<=5; k++)
		{
			list_clients.put(k, new NetDetails(ips[k-1], ports[k-1]));
		}
		
		//list_clients.put(1, new NetDetails("10.176.67.99", 9999));
		//list_clients.put(2, new NetDetails("10.176.67.99", 5555));
		//list_clients.put(3, new NetDetails("10.176.67.99", 6666));
		//list_clients.put(4, new NetDetails("10.176.67.99", 7777));
		//list_clients.put(5, new NetDetails("10.176.67.99", 8888));
		
		
		try
		{
			System.out.println("Connect to other Clients");
			
			
			for(int k=1; k<=5; k++)
			{						
				if( k != client_id)
				{
					String ip = list_clients.get(k).ip_address;
					int port = list_clients.get(k).port;
					
					System.out.println("Create client socket sock[" +k+ "] to conncet to ip: " +ip+ " port: " +port);
					sock[k] = new Socket(ip, port);
					
					oos[k] = new ObjectOutputStream(sock[k].getOutputStream());
					//ois[k] = new ObjectInputStream(sock[k].getInputStream());
				}
			}
			
						
		}
		catch (NoRouteToHostException e)
		{
			e.printStackTrace(System.err);			
			System.out.println(e);					
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
			System.out.println(e);
		}
		
		while(true)
		{		
			synchronized(Client.jq)
			{
				while( Client.jq.size() > 0 )
				{
					//Broadcast message from job Q to other clients
					Message m = Client.jq.poll();
					
					if(m.msg_type.equals("reply"))
					{
						try {
							oos[m.source_clientId].writeObject(m);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						try 
						{							
							for(int j=1; j<=5; j++)
							{						
								if( j != client_id)
								{
									oos[j].writeObject(m);
									oos[j].flush();
								}
							}
							
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
							System.exit(0);
						}
					}
				}
			}
			
		}
		
	}
	
}




/******************************************/
class NetDetails{
	String ip_address;
	int port;
	
	NetDetails(String ip, int port_no)
	{
		this.ip_address = ip;
		this.port = port_no;
	}
}