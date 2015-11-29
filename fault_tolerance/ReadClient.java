import java.io.*;
import java.net.Socket;


public class ReadClient {
	
	int port_number;	
	int client_id;	
	static volatile int ACK = 1;
	int count = 0;
	static volatile int cnt = 0;
	static volatile int crashed_id = 0;
	static int MAX = 5;
	
	
	public static void main(String[] args) {
		
		ReadClient cl = new ReadClient();
						
		cl.client_id = Integer.valueOf(args[0]).intValue();	
		cl.port_number = Integer.valueOf(args[1]).intValue();
				
		Socket[] sock = new Socket[6];
		ObjectOutputStream[]  oos = new ObjectOutputStream[6];
		String[] ips = new String[5];
		int[] ports = new int[5];
		
		try 
		{
			Thread t1 = new Thread(new ReadClientThread(cl.port_number, cl.client_id));
			Thread.sleep(200);
			t1.start();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();			
		}
		
		try
		{								
			MasterConnectThread st = new MasterConnectThread("CLIENT", cl.client_id);
			Thread t = new Thread(st);
			Thread.sleep(1000);
			t.start();			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
			
		}
		
		
		/******************** Connect to the server and send the request *****************/
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
					String ip = ips[k-1];
					int port = ports[k-1];
					
					System.out.println("Create socket sock[" +k+ "] to conncet to ip: " +ip+ " port: " +port);
					sock[k] = new Socket(ip, port);
					
					oos[k] = new ObjectOutputStream(sock[k].getOutputStream());							
				
			}			
			
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();			
		} 
	/*	catch (UnknownHostException e) 
		{
			e.printStackTrace();			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		*/
		
		while(true)
		{
			if(ACK == 1)
			{
				ACK = 0;
				
				System.out.println("*********** Generating  Request Message ************");
				Message m1 = new Message();
				Message m2 = new Message();
				Message m3 = new Message();
				Message m4 = new Message();
				Message m5 = new Message();
				
				m1.msg_type = "REQUEST";
				m1.request = "READ";
				m1.file_name = "file1.txt";
				m1.msg_source = "CLIENT";
				m1.sourceId = cl.client_id;
				m1.msg_dest = "SERVER";
				m1.destId = 1;	
				m1.clientId = cl.client_id;
				
				m2.msg_type = "REQUEST";
				m2.request = "READ";
				m2.file_name = "file1.txt";
				m2.msg_source = "CLIENT";
				m2.sourceId = cl.client_id;
				m2.msg_dest = "SERVER";
				m2.destId = 2;	
				m2.clientId = cl.client_id;
				
				m3.msg_type = "REQUEST";
				m3.request = "READ";
				m3.file_name = "file1.txt";
				m3.msg_source = "CLIENT";
				m3.sourceId = cl.client_id;
				m3.msg_dest = "SERVER";
				m3.destId = 3;	
				m3.clientId = cl.client_id;
				
				m4.msg_type = "REQUEST";
				m4.request = "READ";
				m4.file_name = "file1.txt";
				m4.msg_source = "CLIENT";
				m4.sourceId = cl.client_id;
				m4.msg_dest = "SERVER";
				m4.destId = 4;	
				m4.clientId = cl.client_id;
				
				m5.msg_type = "REQUEST";
				m5.request = "READ";
				m5.file_name = "file1.txt";
				m5.msg_source = "CLIENT";
				m5.sourceId = cl.client_id;
				m5.msg_dest = "SERVER";
				m5.destId = 5;	
				m5.clientId = cl.client_id;
				
				
				try 
				{
					if(ReadClient.crashed_id != 1)
					{
						oos[1].writeObject(m1);
						oos[1].flush();
						System.out.println(" Request sent to server1");
					}
					
					if(ReadClient.crashed_id != 2)
					{
						oos[2].writeObject(m2);
						oos[2].flush();
						System.out.println(" Request sent to server2");
					}
					
					if(ReadClient.crashed_id != 3)
					{
						oos[3].writeObject(m3);
						oos[3].flush();
						System.out.println(" Request sent to server3");
					}
					
					if(ReadClient.crashed_id != 4)
					{
						oos[4].writeObject(m4);
						oos[4].flush();
						System.out.println(" Request sent to server4");
					}
					
					if(ReadClient.crashed_id != 5)
					{
						oos[5].writeObject(m5);
						oos[5].flush();
						System.out.println(" Request sent to server5");
					}
					
				} 
				catch (IOException e) 
				{
					//e.printStackTrace();
					continue;
				}
								
				//cl.count++;
			}
		} // End of while loop		
		
		
	}// End of main
	

}


