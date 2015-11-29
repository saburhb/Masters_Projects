import java.io.*;
import java.util.*;


public class WaitingThread implements Runnable{

	String ip = "";
	int server_id = 0;
	
	WaitingThread(String ip_add, int srv_id)
	{
		this.ip = ip_add;
		this.server_id = srv_id;
	}
	
	
	public void run() {
		
		
		System.out.println("Suspend forwarding message: SUSPEND = " +Server.SUSPEND);
		//int rtt = computeRTT("10.176.67.93");
		try 
		{
			for(int i=1; i<= server_id; i++)
			{
				computeRTT("10.176.67.93"); // testing
			}
			
			//Thread.sleep((server_id * rtt));
						
			if(Server.wait_flag1 == 0)
			{
				//notify other servers declaring itself as new head
				Server.wait_flag1 = 1;
				
				Message m = new Message();
				m.msg_type = "NOTIFICATION";
				m.newhead = server_id;
				m.time_stamp[server_id] = Server.clock;
				m.msg_dest = "SERVER";
				m.crashed_server_id = Server.crashed_id;
																			
				Server.mq.add(m);
				
				for(int i=1; i<= (5 - server_id); i++)
				{
					computeRTT("10.176.67.93"); // testing
				}
				
				//Thread.sleep((5 - server_id) * rtt);
				
				/*****************************************/
				if(Server.crashed_id != 5)
				{
					if(server_id != Server.newHead)
					{
						System.out.println(" This server is not newHead: delete all history");
						//delete all history
						if(Server.al.size() > 0)
						{
							for(int i=0; i<Server.al.size(); i++)
							{
								Server.al.remove(i);
							}
						}								
					}
				}
				else // if crashed id is TAIL
				{
					if(Server.al.size() > 0)
					{
						for(int i=0; i<Server.al.size(); i++)
						{
							if(Server.al.get(i).head_server != server_id)
							{
								Server.al.remove(i);
							}
						}
					}
										
				}
				
				Message m1 = new Message();
				m1.msg_type = "UNBLOCK";
				m1.newhead = server_id;
				m1.time_stamp[server_id] = Server.clock;
				m1.msg_dest = "SERVER";
				m1.crashed_server_id = Server.crashed_id;
																			
				Server.mq.add(m1);
				
				
			}
			else
			{
				for(int i=1; i<= (5 - server_id); i++)
				{
					computeRTT("10.176.67.93"); // testing
				}
				
				//Thread.sleep((5 - server_id) * rtt);
				
				/*****************************************/
				if(Server.crashed_id != 5)
				{
					if(server_id != Server.newHead)
					{
						System.out.println(" This server is not newHead: delete all history");
						//delete all history
						if(Server.al.size() > 0)
						{
							for(int i=0; i<Server.al.size(); i++)
							{
								Server.al.remove(i);
							}
						}								
					}
				}
				else // if crashed id is TAIL
				{
					if(Server.al.size() > 0)
					{
						for(int i=0; i<Server.al.size(); i++)
						{
							if(Server.al.get(i).head_server != server_id)
							{
								Server.al.remove(i);
							}
						}
					}
					
					
				}
				
				Message m1 = new Message();
				m1.msg_type = "UNBLOCK";
				m1.newhead = server_id;
				m1.time_stamp[server_id] = Server.clock;
				m1.msg_dest = "SERVER";
				m1.crashed_server_id = Server.crashed_id;
				
				System.out.println("Sending unblock message to MASTER");															
				Server.mq.add(m1);
							
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	/*************************************************************/
	public static int computeRTT( String ip)
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
	
	

} // end of class
