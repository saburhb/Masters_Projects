import java.io.*;
import java.net.*;

class ServerPartThread implements Runnable{

	int port;
	int client_id;
	int clock;
	
	ServerSocket servsock = null;
	Socket consock = null;	

	
	ServerPartThread(int port_number, int cli_Id, int tc)
	{
		this.port = port_number;
		this.client_id = cli_Id;
		this.clock = tc;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
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
				System.out.println("Server socket of the client is created");
				
				ServerPartChildThread st = new ServerPartChildThread(consock,client_id,clock);
				Thread.sleep(2000);
				st.start();			
				
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			} catch (InterruptedException e) 
			{					
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		
	}

}



/*****************************************************/

class ServerPartChildThread extends Thread {

	Socket child;
	ObjectInputStream ois= null;
	//ObjectOutputStream oos = null;
	int client_id;
	int clock;

	
	public ServerPartChildThread(Socket consock, int cli_Id, int cl) {
		this.child = consock;
		this.client_id = cli_Id;
		this.clock = cl;
						
		try 
		{
			ois = new ObjectInputStream(child.getInputStream());	
			//oos = new ObjectOutputStream(child.getOutputStream());
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		
		}
		
	}

	@Override
	public void run() {
		while(true)
		{
			try 
			{
				System.out.println("read object from connection socket");
				
				
				Message message = (Message)(ois.readObject());
				
				System.out.println("Message received from client: "+ message.source_clientId + "  for file" +message.file_id);
		        
		        System.out.println("*********** MESSAGE RECEIVED FROM OTHER CLIENTS ***********");
				System.out.println(" Message Id: " +message.message_id);
				System.out.println(" Message Timestamp: " + message.time_stamp);
				System.out.println(" Request from Client: " +message.source_clientId);
				System.out.println(" Message type: " +message.msg_type);
				System.out.println(" Message Conent: " +message.content);
				
				if(message.file_id == 1)
		        {
		        	if(message.msg_type.equals("request"))
		        	{	
		        		Message msg_rep = new Message();   
		        		
		        		msg_rep.message_id = message.message_id;
		        		msg_rep.msg_type = "reply";		        	
		        		clock = Math.max(message.time_stamp, ++clock);
		        		msg_rep.time_stamp = clock;
		        		msg_rep.source_clientId = message.source_clientId;
		        		msg_rep.reply_client = client_id;
		        		msg_rep.file_id = message.file_id;
		        		
		        		synchronized(Client.pq1)
		        		{
		        			Client.pq1.add(message);
		        		}
		        		
		        		Client.jq.add(msg_rep);
		        		//oos.writeObject(msg_rep);
		        		//oos.flush();
		        		System.out.println("Sending reply message for reuest from Client" +msg_rep.source_clientId + "for msg id: " +msg_rep.message_id);
		        	}
		        	else if(message.msg_type.compareTo("reply") == 0)
		        	{
		        		System.out.println("Reply Received from Client" +message.reply_client +"for my request msg id:" +message.message_id);
		        		synchronized(Client.l1)
		        		{
		        			clock = Math.max(message.time_stamp, ++clock);
		        			Client.l1.add(message);
		        		}        		
		        	}
		        	else if(message.msg_type.equals("release"))
		        	{
		        		//remove head from queue
		        		synchronized(Client.pq1)
		        		{
		        			clock = Math.max(message.time_stamp, ++clock);
		        			Client.pq1.poll();
		        		}
		        		
		        	}
		        	
		        	System.out.println("********* TEST 1 ***********");
		        }
				else if(message.file_id == 2)
		        {
		        	if(message.msg_type.equals("request"))
		        	{	
		        		Message msg_rep = new Message();   
		        		
		        		msg_rep.message_id = message.message_id;
		        		msg_rep.msg_type = "reply";
		        		clock = Math.max(message.time_stamp, ++clock);
		        		msg_rep.time_stamp = clock;
		        		msg_rep.source_clientId = message.source_clientId;
		        		msg_rep.reply_client = client_id;
		        		msg_rep.file_id = message.file_id;
		        		
		        		synchronized(Client.pq2)
		        		{
		        			Client.pq2.add(message);
		        		}
		        		
		        		Client.jq.add(msg_rep);
		        		//oos.writeObject(msg_rep);
		        		//oos.flush();
		        		System.out.println("Sending reply message for reuest from Client" +msg_rep.source_clientId + "for msg id: " +msg_rep.message_id);
		        	}
		        	else if(message.msg_type.compareTo("reply") == 0)
		        	{
		        		System.out.println("Reply Received from Client" +message.reply_client +"for my request msg id:" +message.message_id);
		        		synchronized(Client.l2)
		        		{
		        			clock = Math.max(message.time_stamp, ++clock);
		        			Client.l2.add(message);
		        		}        		
		        	}
		        	else if(message.msg_type.equals("release"))
		        	{
		        		//remove head from queue
		        		synchronized(Client.pq2)
		        		{
		        			clock = Math.max(message.time_stamp, ++clock);
		        			Client.pq2.poll();
		        		}
		        	}
		        	
		        	System.out.println("********* TEST 2 ***********");
		        }
				else if(message.file_id == 3)
		        {
		        	if(message.msg_type.equals("request"))
		        	{	
		        		Message msg_rep = new Message();   
		        		
		        		msg_rep.message_id = message.message_id;
		        		msg_rep.msg_type = "reply";
		        		clock = Math.max(message.time_stamp, ++clock);
		        		msg_rep.time_stamp = clock;
		        		msg_rep.source_clientId = message.source_clientId;
		        		msg_rep.reply_client = client_id;
		        		msg_rep.file_id = message.file_id;
		        		
		        		synchronized(Client.pq3)
		        		{
		        			Client.pq3.add(message);
		        		}
		        			
		        		Client.jq.add(msg_rep);
		        		//oos.writeObject(msg_rep);
		        		//oos.flush();
		        		System.out.println("Sending reply message for reuest from Client" +msg_rep.source_clientId + "for msg id: " +msg_rep.message_id);
		        	}
		        	else if(message.msg_type.compareTo("reply") == 0)
		        	{
		        		System.out.println("Reply Received from Client" +message.reply_client +"for my request msg id:" +message.message_id);
		        		clock = Math.max(message.time_stamp, ++clock);
		        		Client.l3.add(message);	        		
		        	}
		        	else if(message.msg_type.equals("release"))
		        	{
		        		//remove head from queue
		        		clock = Math.max(message.time_stamp, ++clock);
		        		Client.pq3.poll();
		        	}
		        	
		        	System.out.println("********* TEST 3 ***********");
		        }			
				
				
			}
			catch(EOFException e)
			{
				System.out.println("ERROR IN RECEIVING FILE IN READ OBJECT");
				System.out.println(e);
			}
			catch (IOException e)
		    {
				System.out.println(e);
				
		    } 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				
			}
			
		}
	}
	
}