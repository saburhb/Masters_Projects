import java.io.*;
import java.net.*;

import java.util.*;


class MutualExclusion implements Runnable{

	int client_id;
	int clock;
	ObjectOutputStream[] oos = new ObjectOutputStream[3];
	ObjectInputStream[] ois = new ObjectInputStream[3];
	
	MutualExclusion(int cli_Id, int tc)
	{
		this.client_id = cli_Id;		
		this.clock = tc;
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int iRet = 0;
		
		Socket[] sock = new Socket[3];		
		
		try 
		{
			sock[0] = new Socket("10.176.67.98", 4567);
			sock[1] = new Socket("10.176.67.98", 5678);
			sock[2] = new Socket("10.176.67.98", 6789);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			System.exit(0);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		try
		{
			for(int i=0; i<3; i++)
			{
				oos[i] = new ObjectOutputStream(sock[i].getOutputStream());
				ois[i] = new ObjectInputStream(sock[i].getInputStream());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
		}
		
		try
		{
			Message me = new Message();
			
			me.msg_type = "enquiry";
			me.source_clientId = client_id;
			
			for(int i=0; i<3; i++)
			{
				oos[i].writeObject(me);
				oos[i].flush();				
			}
			
			for(int j=0; j<3; j++)
			{
				Message mr = new Message();
				mr = (Message)(ois[j].readObject());
				System.out.println("************ FILES IN TEH SERVER "+mr.source_server +"**********");
				System.out.println(mr.content);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		while(true)
		{	
			try
			{
			synchronized(Client.pq1)
			{
				if(Client.pq1.size() > 0)
				{
					if(Client.pq1.peek().source_clientId == client_id)
					{
						synchronized(Client.l1)
						{
							if(check_reply(Client.l1, Client.pq1.peek().message_id, Client.pq1.peek().source_clientId) == 0)
							{
								System.out.println("Client" +client_id+ "can enter the critical section for file 1");
								iRet = sendReqToserver(Client.pq1.peek());
								if(iRet == 1)
								{
									Message rel_msg = new Message();
									
									rel_msg.message_id = Client.pq1.peek().message_id;
									rel_msg.msg_type = "release";
									rel_msg.time_stamp = ++clock;
									rel_msg.source_clientId = client_id;
									rel_msg.file_id = Client.pq1.peek().file_id;
									
									synchronized(Client.jq)
									{
										Client.jq.add(rel_msg);
									}
									
									Client.pq1.poll();
									iRet =0;
								}
							}
						}
					}
				}
			}
			
			synchronized(Client.pq2)
			{
				if(Client.pq2.size() > 0)
				{
					if(Client.pq2.peek().source_clientId == client_id)
					{
						synchronized(Client.l2)
						{
							if(check_reply(Client.l2, Client.pq2.peek().message_id, Client.pq2.peek().source_clientId) == 0)
							{
								System.out.println("Client" +client_id+ "can enter the critical section for file 2");
								iRet = sendReqToserver(Client.pq2.peek());
								if(iRet == 1)
								{
									Message rel_msg = new Message();
									
									rel_msg.message_id = Client.pq2.peek().message_id;
									rel_msg.msg_type = "release";
									rel_msg.time_stamp = ++clock;
									rel_msg.source_clientId = client_id;
									rel_msg.file_id = Client.pq2.peek().file_id;
									
									synchronized(Client.jq)
									{
										Client.jq.add(rel_msg);
									}
									
									Client.pq2.poll();
									iRet = 0;
								}
							}
						}
					}
				}
			}
			
			synchronized(Client.pq3)
			{
				if(Client.pq3.size() > 0)
				{
					if(Client.pq3.peek().source_clientId == client_id)
					{
						synchronized(Client.l3)
						{
							if(check_reply(Client.l3, Client.pq3.peek().message_id, Client.pq2.peek().source_clientId) == 0)
							{
								System.out.println("Client" +client_id+  "can enter the critical section for file3");
								iRet = sendReqToserver(Client.pq1.peek());
								if(iRet == 1)
								{
									Message rel_msg = new Message();
									
									rel_msg.message_id = Client.pq3.peek().message_id;
									rel_msg.msg_type = "release";
									rel_msg.time_stamp = ++clock;
									rel_msg.source_clientId = client_id;
									rel_msg.file_id = Client.pq3.peek().file_id;
									
									synchronized(Client.jq)
									{
										Client.jq.add(rel_msg);
									}
									
									Client.pq3.poll();
									iRet = 0;
								}
							}
						}
					}
				}
			}
			
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	}
	
	
	public int check_reply(ArrayList<Message> l, int msg_id, int client_id)
	{
		Message m;
			
		
		int flag[] = new int[6];
		
		for(int k=1; k<=5; k++)
		{
			if(client_id == k )
				flag[k] = 1;
			
			//System.out.println("************* Checking the REPLY MESSAGE LIST ************");
			//System.out.println(l.size());
			for(int i=0; i<l.size(); i++)
			{
				m = l.get(i);
				//System.out.println(+i + "th Reply: from Client"+ m.reply_client + "for message " + m.message_id+ " of client" +m.source_clientId );
				if((m.message_id == msg_id) && (m.source_clientId == client_id) && (m.reply_client == k))
				{
					flag[k] = 1;
					break;
				}
			}
		}
				
		if((flag[1]==1) && (flag[2]==1) && (flag[3]==1) && (flag[4]==1) && (flag[5]==1)) 
		{
			for(int k=1; k<=5; k++)
			{			
				System.out.println(l.size());
				for(int i=0; i<l.size(); i++)
				{
					m = l.get(i);					
					if((m.message_id == msg_id) && (m.source_clientId == client_id) && (m.reply_client == k))
					{
						l.remove(i);
						break;
					}
				}
			}
			return 0;
		}
		else return -1;
		
	}
	
	
	/***********************************************/
	public int sendReqToserver(Message m)
	{
		int RELEASE = 0;
		int index;
		
		Random rd = new Random();
		index = rd.nextInt(3);
		
		if( m.request != null)
		{
			if(m.request.equals("READ"))
			{
				try
				{
					oos[index].writeObject(m);
					oos[index].flush();
							
					Message mr = (Message)(ois[index].readObject());
					System.out.println("*************** MESSAGE RECEIVED FROM SERVER *******************");
					System.out.println(" Message Id: " +mr.message_id);
					System.out.println(" Request from Client: " +mr.source_clientId);
					System.out.println(" Request processed by Server: " +mr.source_server);
					System.out.println(" Message Conent: " +mr.content);
									
					if(mr.release == 1)
					{
						RELEASE = 1;
					}
					else
					{
						RELEASE = 0;
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
			}
			else if(m.request.equals("WRITE"))
			{
				try
				{
					for(int i=0; i<3; i++)
					{
						oos[i].writeObject(m);
						oos[i].flush();
					}
					
					for(int j=0; j<3; j++)
					{
						Message mr = (Message)(ois[j].readObject());
						System.out.println("*************** MESSAGE RECEIVED FROM SERVER *******************");
						System.out.println(" Message Id: " +mr.message_id);
						System.out.println(" Request from Client: " +mr.source_clientId);
						System.out.println(" Request processed by Server: " +mr.source_server);
						System.out.println(" Message Conent: " +mr.content);
										
						if(mr.release == 1)
						{
							RELEASE = 1;
						}
						else
						{
							RELEASE = 0;
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();	
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		return RELEASE;
	} // end senrequestToServer

}
	
	


