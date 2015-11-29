
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


class ReadClientThread implements Runnable{

	int port_number;
	int client_id;
	ServerSocket servclisock = null;
	Socket consock = null;
	ObjectInputStream  oic = null;
	
	
	ReadClientThread(int port, int cid)
	{
		this.port_number = port;
		this.client_id = cid;		
	}
	
	public void run() {
		
		try
		{
			System.out.println("Creating Listen socket at port# " + port_number);
			servclisock  = new ServerSocket(port_number);						
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
				consock = servclisock.accept();				
				
				Thread t2 = new Thread(new ReadClientChildThread(consock));				
				t2.start();					
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			} 			
			
		}
	}

}



/****************************************************************/

class ReadClientChildThread implements Runnable{

	Socket child;	
	ObjectInputStream  oic = null;
	
	ReadClientChildThread(Socket consock)
	{
		this.child = consock;
				
		try 
		{
			oic = new ObjectInputStream(child.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
				
		//while(true)
		//{
			try
			{
				Message message = (Message)(oic.readObject());
				
				if(!message.ack.equals(""))
				{
					System.out.println("*** ACK Received from Server ***");
					System.out.println(" @@@@@@@@@ ACK: " + message.ack + "@@@@@@@@@");
					ReadClient.cnt++;
					
					if(ReadClient.cnt == 5)
					{
						ReadClient.ACK= 1;
						System.out.println("*** Setting ACK flag to 1 ***");
						ReadClient.cnt = 0;
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				//continue;
				
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		
		//}
	}
	
}








