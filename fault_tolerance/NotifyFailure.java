import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class NotifyFailure {
	
	static Socket[] sock = new Socket[6];
	static ObjectOutputStream[]  oos = new ObjectOutputStream[6];
	
	public static void notifyFail(int server_id)
	{
		String[] ips = new String[5];
		int[] ports = new int[5];
		
		System.out.println("Server #" + server_id + " has died : Notify to all");
		
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
		
		for(int j=1; j<=5; j++)
		{	
			Message m = new Message();
			m.msg_type = "NOTIFY";
			m.msg_source = "SERVER";
			m.sourceId = 0;
			m.crashed_server_id = server_id;
			
									
			if( j != server_id)
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
	

}
