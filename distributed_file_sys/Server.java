import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Server {
	
	public static final int TAIL_ID = 5;	
	int port;
	int server_id;
	static int clock = 0;
	static int seq_num = 0;
	
		
	static volatile PriorityQueue<Message> jq = new PriorityQueue<Message>(50, new MComparator());
	static volatile ArrayList<Message> al = new ArrayList<Message>();
	static volatile ArrayList<Message> ml = new ArrayList<Message>();
	static volatile PriorityQueue<Message> tailq = new PriorityQueue<Message>(30, new MessageComparator());
	
	
	public static void main(String[] args) {
					
		/* (1) Create Server socket and listen on the port */
		Server serv = new Server();
	    
		serv.server_id = Integer.valueOf(args[0]).intValue();
		serv.port = Integer.valueOf(args[1]).intValue();
		
		
		/* (2) Create thread for listening socket */
		try
		{								
			ServerThread st = new ServerThread(serv.port, serv.server_id);
			Thread t = new Thread(st);
			//Thread.sleep(2000);
			t.start();			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
			
		}	
		
		/* (3) Create thread to connect to other server and process */
		try
		{								
			ServerConnectThread st2 = new ServerConnectThread(serv.server_id);
			Thread t2 = new Thread(st2);
			Thread.sleep(20000);
			t2.start();
			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
		}
		
		
		/* (4) Create a thread for processing if it is a TAIL server */
		try
		{	
			if(serv.server_id == 5)
			{
				TailThread st1 = new TailThread(serv.server_id);
				Thread t1 = new Thread(st1);
				Thread.sleep(2000);
				t1.start();
			}
			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
		}
		
		
	} // end of main

} // end of class





/**********************************************/
class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int message_id; 
	int [] time_stamp = new int[6];
	String msg_type;
	String request;
	String msg_source;
	String msg_dest;
	int sourceId;
	int destId;
	int clientId;
	String ack;
	int head_server;
	String file_name;
}


/*************************************************/
class MComparator implements Comparator<Message>
{
	public int compare(Message m1, Message m2) {
		return 1;
	}
}


/*********************************************************/
class MessageComparator implements Comparator<Message>
{
	int ret;
	int eq = 0;
	int flag1 = 0;
	int flag2 = 0;
	public int compare(Message m1, Message m2) {
				
		for(int i=0; i<=5; i++)
		{
			if(m1.time_stamp[i] == m2.time_stamp[i])
			{
				flag1++;
				flag2++;
			}
			else if(m1.time_stamp[i] < m2.time_stamp[i])
			{
				flag1++;
			}
			else if(m1.time_stamp[i] > m2.time_stamp[i])
			{
				flag2++;					
			}
		}
		
		if((flag1 == 6) && (flag2 != 6))
		{
			return -1;
		}
		else if((flag2 == 6) && (flag1 != 6))
		{
			return 1;
		}
		else
		{
			if(m1.head_server < m2.head_server)
				return -1;
			else if(m1.head_server > m2.head_server)
				return 1;
			else
				return 0;
		}		
			
	}
}
