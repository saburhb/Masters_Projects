import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


class MasterConnectThread implements Runnable{

	int port;
	String ip ;
	int server_id;
	Socket sock = null;
	ObjectOutputStream  oos = null;
	String source;
	int source_id = 0;
	
	MasterConnectThread(String msg_source, int id)
	{
		this.source = msg_source;
		this.source_id = id;
	}
	
	
	public void run() {
		
		/* (1) Connect to Master */
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader("config_serv.txt"));
			String info = "";			
			
			for (int i=0; i < 12; i++)
			{
				try 
			    {
					info = br.readLine();
			    } 
			    catch (IOException e) 
			    {					
			    	e.printStackTrace();
				}
			    if( i == 10)
			    {			    	 
			    	port = Integer.valueOf(info).intValue();
			    }
			    if( i == 11 )
			    {
			    	ip = info;
			    }
			}

			try 
			{
				br.close();
				sock = new Socket(ip, port);
				oos = new ObjectOutputStream(sock.getOutputStream());		
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	
			Message m = new Message();
			m.msg_type = "HEARTBEAT";
			m.msg_source = source;
			m.sourceId = source_id;
			
			try 
			{
				oos.writeObject(m);
				oos.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.out.println("sending message to master failed");
			}			
		
	
			
			while(true)
			{
				if(Server.mq.size() > 0 )
				{
					if(Server.mq.peek() != null)
					{
						Message m1 = (Message)Server.mq.poll();
						Socket sock_m = null;
						try 
						{
							sock_m = new Socket(ip, port);
							oos = new ObjectOutputStream(sock_m.getOutputStream());
							
							oos.writeObject(m1);
							oos.flush();
						} 
						catch (UnknownHostException e) 
						{
							e.printStackTrace();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}							
						
					}
				}
			}
	}
	

}
