import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


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
				
				if(message.msg_type.equals("NOTIFICATION"))
				{
					System.out.println("The Server " + message.crashed_server_id + " has crashed");	
					Client.crashed_id = message.crashed_server_id;
					
					for(int i=1; i<=8; i++)
					{
						computeRTT1("10.176.67.93"); // testing
					}
					if(Client.ACK == 0)
					{
						Client.ACK = 1;
					}
				}
				else
				{
					if(!message.ack.equals(""))
					{
						System.out.println("*** ACK Received from Server ***");
						System.out.println(" @@@@@@@@@ ACK: " + message.ack + "@@@@@@@@@");
						Client.ACK= 1;
						System.out.println("*** Setting ACK flag to 1 ***");
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
								
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		
		//}
	}
	
	/*************************************************************/
	public static int computeRTT1( String ip)
	{
		System.out.println("ComputeRTT: Ping to IP " + ip);
		//String pingResult = "";
		int RTT = 0;
		String pingCmd = null;
		pingCmd = "ping " + ip;
	    int num_iter = 10; // gather 10 samples to take average
		try 
		{
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);
	
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String inputLine;
	        int avgRTT =0;
	        inputLine = in.readLine();
	        for(int i=0;i<num_iter;i++)
	        {
	            if((inputLine = in.readLine()) != null){
	               // System.out.println(inputLine);
	              //  pingResult += inputLine;
	                StringTokenizer strtok = new StringTokenizer(inputLine);
	                String nextStr="";
	                do{
	                    nextStr = strtok.nextToken();
	                    //System.out.println(nextStr);
	                    if(nextStr.startsWith("time")){
	                        avgRTT += Double.parseDouble(nextStr.substring(5));
	                        //System.out.println(""+avgRTT+"ms");
	                        break;
	                    }
	                }while(!nextStr.startsWith("time"));
	            }
	        }
	        avgRTT /= num_iter;
	        RTT = avgRTT + 1;
	        System.out.println("RTT to "+ ip+":"+avgRTT+"ms");	        
			in.close();
			
			
		}//try
		catch (IOException e) 
		{
			System.out.println(e);
		}
		catch(NoSuchElementException e)
		{
			System.out.println(e);
	    }
		return(RTT);		
	} //end of method
	
}



