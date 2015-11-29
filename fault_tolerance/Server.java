import java.util.ArrayList;
import java.util.PriorityQueue;

public class Server {
	
	public static int TAIL_ID = 5;	
	int port;
	int server_id;
	static int clock = 0;
	static int seq_num = 0;
	static int wait_flag1 = 0;
	static volatile int crashed_id = 0;
	static volatile int notification = 0; 
	static volatile int SUSPEND = 0;
	static volatile int newHead = 0;
	static int unique_num = 0;
		
	static volatile PriorityQueue<Message> jq = new PriorityQueue<Message>(50, new MComparator());
	static volatile ArrayList<Message> al = new ArrayList<Message>();
	static volatile ArrayList<Message> ml = new ArrayList<Message>();
	static volatile PriorityQueue<Message> tailq = new PriorityQueue<Message>(30, new MessageComparator());
	static volatile PriorityQueue<Message> mq = new PriorityQueue<Message>(10, new MComparator());
	static volatile PriorityQueue<Message> tempq = new PriorityQueue<Message>(30, new MComparator());
	
	
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
		
		/* (3) */
		try
		{								
			MasterConnectThread st = new MasterConnectThread("SERVER", serv.server_id);
			Thread t = new Thread(st);
			Thread.sleep(5000);
			t.start();			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
			
		}
		
		
		/* (4) Create thread to connect to other server and process */
		try
		{								
			ServerConnectThread st2 = new ServerConnectThread(serv.server_id);
			Thread t2 = new Thread(st2);
			Thread.sleep(30000);
			t2.start();
			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
		}
		
		
		/* (5) Create a thread for processing if it is a TAIL server */
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