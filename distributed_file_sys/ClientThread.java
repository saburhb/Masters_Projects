import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientThread implements Runnable{

	int port_number;
	int client_id;
	ServerSocket servclisock = null;
	Socket consock = null;
	ObjectInputStream  oic = null;
	
	
	ClientThread(int port, int cid)
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
				
				//try 
				//{
					Thread t2 = new Thread(new ClientChildThread(consock));
					//Thread.sleep(4000);
					t2.start();
				//} 
				//catch (InterruptedException e) 
				//{
				//	e.printStackTrace();
				//	System.exit(0);
				//}
				
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

class ClientChildThread implements Runnable{

	Socket child;	
	ObjectInputStream  oic = null;
	
	ClientChildThread(Socket consock)
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
					Client.ACK= 1;
					System.out.println("*** Setting ACK flag to 1 ***");
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(0);
				
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		
		//}
	}
	
}



