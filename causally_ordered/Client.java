import java.io.*;
import java.util.*;

public class Client {

	int port_number;
	int tc1 = 0;
	int client_id;
	int fileId;
	
	static ArrayList<Message> l1 = new ArrayList<Message>(); 
	static ArrayList<Message> l2 = new ArrayList<Message>();
	static ArrayList<Message> l3 = new ArrayList<Message>();
	static ArrayList<Message> reply_list = new ArrayList<Message>();
	
	
	static PriorityQueue<Message> pq1 = new PriorityQueue<Message>(20, new MessageComparator());
	static PriorityQueue<Message> pq2 = new PriorityQueue<Message>(20, new MessageComparator());
	static PriorityQueue<Message> pq3 = new PriorityQueue<Message>(20, new MessageComparator());
	
	//HashMap<Integer, PriorityQueue<Message>> req_map = new HashMap<Integer, PriorityQueue<Message>>();
	//HashMap<Integer, ArrayList<Message>> msg_map = new HashMap<Integer, ArrayList<Message>>();
	
	public static PriorityQueue<Message> jq = new PriorityQueue<Message>(30, new MComparator());
		
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Client client1 = new Client();
		
		client1.port_number = Integer.valueOf(args[0]).intValue();		
		client1.client_id = Integer.valueOf(args[1]).intValue();
		
		Thread ts = new Thread(new ServerPartThread(client1.port_number, client1.client_id, client1.tc1));
		
		try
		{
			ts.start();
		}
		catch(Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
		
	
		try
		{
			Thread t = new Thread(new ClientPartThread(client1.port_number, client1.client_id));
			Thread.sleep(20000);
			t.start();
		}
		catch(Exception e)
		{
			System.out.println(e);
			System.exit(0);
		}
		
		try 
		{
			Thread t1 = new Thread(new MutualExclusion(client1.client_id, client1.tc1));
			Thread.sleep(30000);
			t1.start();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		
		//generate request and put in job queue
		for(int i=1; i<=5; i++)
		{
			System.out.println("*********** Generating  Request Message ************");
			Message m = new Message();
			int index;
			
			Random rd = new Random();
			index = rd.nextInt(2);
					
			System.out.println(" Random generated Index = " +index);
			
			m.message_id = i;
			m.msg_type = "request";
			m.file_id = i%3 + 1;
			m.source_clientId = client1.client_id;
			m.time_stamp = ++(client1.tc1);
			if(index == 0)
			{
				m.request = "READ";
			}
			else if( index == 1)
			{
				m.request = "WRITE";
				m.content = "Client C"+m.source_clientId +"--> Write the message : "+ m.message_id+ " *** Request sent at time : "+m.time_stamp ;
			}
					
			
			System.out.println(" Message Id: " +m.message_id);
			System.out.println(" Message Timestamp: " + m.time_stamp);
			System.out.println(" Request from Client: " +m.source_clientId);
			System.out.println(" Message type: " +m.msg_type);
			System.out.println(" Message Conent: " +m.content);
			
			synchronized(jq)
			{
				jq.add(m);
			}
			
			if(m.file_id == 1)
			{
				synchronized(pq1)
				{
					pq1.add(m);
				}
				
			}
			else if(m.file_id == 2)
			{
				synchronized(pq2)
				{
					pq2.add(m);
				}
				
			}
			else if(m.file_id == 3)
			{
				synchronized(pq3)
				{
					pq3.add(m);
				}
				
			}
			
			
		}

	}

}




/**********************************************/
class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int message_id; 
	int time_stamp;
	String msg_type;
	String request;
	String content;
	int source_clientId;
	int reply_client;
	int release;
	int source_server;
	int file_id;
}


/*********************************************************/
class MessageComparator implements Comparator<Message>
{
	@Override
	public int compare(Message m1, Message m2) {
		
		if(m1.time_stamp < m2.time_stamp)
			return -1;
		if(m1.time_stamp > m2.time_stamp)
			return 1;
		if(m1.time_stamp == m2.time_stamp)
		{
			if(m1.source_clientId < m2.source_clientId)
				return -1;
			else if(m1.source_clientId > m2.source_clientId)
				return 1;
			else
				return 0;
		}		
		return 0;
	}
}

/*************************************************************/
class MComparator implements Comparator<Message>
{
	public int compare(Message m1, Message m2) {
		return 1;
	}
}

