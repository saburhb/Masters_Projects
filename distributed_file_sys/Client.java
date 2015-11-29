import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client {

	int port_number;	
	int client_id;	
	static volatile int ACK = 1;
	int count = 0;
	
	
	public static void main(String[] args) {
	
		Client cl = new Client();
						
		cl.client_id = Integer.valueOf(args[0]).intValue();	
		cl.port_number = Integer.valueOf(args[1]).intValue();
		int servId;
		
		try 
		{
			Thread t1 = new Thread(new ClientThread(cl.port_number, cl.client_id));
			Thread.sleep(200);
			t1.start();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();			
		}
		
		while(cl.count < 100)
		{
			if(ACK == 1)
			{
				ACK = 0;
				Random rd = new Random();
				servId = rd.nextInt(5) + 1;
				
				Random r1 = new Random();
				int flag = r1.nextInt(2);
				
				System.out.println("*********** Generating  Request Message ************");
				Message m = new Message();
				
				m.msg_type = "REQUEST";
				if(flag == 0)
				{
					m.request = "READ";
				}
				else
				{
					m.request = "WRITE";  // Add for READ later
				}
				m.file_name = "file1.txt";
				m.msg_source = "CLIENT";
				m.sourceId = cl.client_id;
				m.msg_dest = "SERVER";
				m.destId = servId;	
				m.clientId = cl.client_id;
				
				System.out.println(" Message Type: " + m.msg_type);
				System.out.println(" Request for: " + m.request);
				System.out.println(" Request from Client: " +m.sourceId);
				System.out.println(" Request to Server: " +m.destId);
				System.out.println(" Operation Request on File : " +m.file_name);
								
				
				/******************** Connect to the server and send the request *****************/
				int port_serv = 0;
				String ip_serv = "";
								
				try 
				{
					BufferedReader br = new BufferedReader(new FileReader("config_serv.txt"));
					String s = "";
											
					for(int k=0; k<10; k++)
					{
						try 
					    {
							s = br.readLine();
					    } 
					    catch (IOException e) 
					    {					
					    	e.printStackTrace();
						}
						
						if((servId - 1)*2 == k)
						{
							port_serv = Integer.valueOf(s).intValue();
						}
						if((servId*2 -1) == k)
						{
							ip_serv = s;
						}
					}
					
					//create connection to the client
					System.out.println(" Connect to Server at " + ip_serv + " on port " +port_serv );
					if((port_serv != 0) && !(ip_serv.equals("")))
					{
						Socket sock_serv = null;
						ObjectOutputStream  oos = null;
						
						sock_serv = new Socket(ip_serv, port_serv);						
						oos = new ObjectOutputStream(sock_serv.getOutputStream());
						
						oos.writeObject(m);
						oos.flush();
						
						//oos.close();
						//sock_serv.close();
						System.out.println(" Request sent to server");
					}
					else
					{
						System.out.println("ip and port are not valid");
					}
				}
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}  
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				//end of msg send to server
				 
				cl.count++;
			}//end of check for ACK
		}//end of for loop
	}// end of main
	
}
