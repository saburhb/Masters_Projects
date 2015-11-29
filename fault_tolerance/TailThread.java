import java.io.*;


class TailThread implements Runnable{

	int server_id;
	int iRet;
	
		
	TailThread(int srv_id)
	{		
		this.server_id = srv_id;
				
	}
	
	
	public void run() {
				
		while(true)
		{	
			
			//read tailq and process
			while(Server.tailq.size() > 0)
			{
				Message m = Server.tailq.poll();
				// write the content of this message in file or Read from the file
				
				
				Message reply_msg = new Message();
				
				
				if(m.request.equals("WRITE"))
				{
					reply_msg.msg_type = "COMMIT";
					reply_msg.msg_source = "SERVER";
					reply_msg.sourceId = Server.TAIL_ID;
					reply_msg.msg_dest = "SERVER";
					if(Server.crashed_id != (Server.TAIL_ID - 1))
					{
						reply_msg.destId = (Server.TAIL_ID - 1);
					}
					else
					{
						reply_msg.destId = (Server.TAIL_ID - 2);
					}
					reply_msg.message_id = m.message_id;
					reply_msg.head_server = m.head_server;
					reply_msg.file_name = m.file_name;
					reply_msg.request = m.request;
					reply_msg.clientId = m.clientId;
					
					for(int i=0; i<=5; i++)
					{
						reply_msg.time_stamp[i] = m.time_stamp[i];
					}
					
					m.unique_id = ++(Server.unique_num);
					reply_msg.unique_id = m.unique_id;
					
					iRet = FileOperation.fileWrite(m, Server.TAIL_ID , Server.clock);
					if(iRet == 0)
					{
						reply_msg.ack = "SUCCESS"; // or FAIL depending on return value of write in file
					}
					else
					{
						reply_msg.ack = "FAIL";
					}
				}
				/*else if(m.request.equals("READ"))
				{
					FileOperation.fileRead(m, 5);
					
					reply_msg.msg_type = "COMMIT";
					reply_msg.msg_source = "SERVER";
					reply_msg.sourceId = 5;
					reply_msg.msg_dest = "CLIENT";
					reply_msg.destId = m.destId;
					reply_msg.file_name = m.file_name;
					reply_msg.request = m.request;
					reply_msg.ack = m.ack;
				}*/
				
				Server.jq.add(reply_msg);
			}
		}
	}

}



/***********************************************/
class FileOperation {
	
	Message m;
	
	
	FileOperation(Message msg)
	{
		this.m = msg;
	}
	
	public synchronized static int fileWrite(Message m, int server_id, int clock)
	{
		System.out.println("******** WRITING IN FILE *******");
		//String file1;
		//file1 = m.file_name;
		String  str = "SERVER ID: <" + server_id + "> --- CLIENT ID: " + m.clientId +" --- HEAD SERVER: " +m.head_server+ " ---SEQUENCE NUMBER: " + m.message_id + " --- at LOCAL TIME: " + clock+ "---UNIQUE ID : " + m.unique_id;
		
		System.out.println(" Content to be written in file " + m.file_name + " : " + str);
		
		String f = "S" + server_id + "/" + m.file_name;  //testing
		File file1 = new File(f);
		
		FileWriter fstream;
		try 
		{
			fstream = new FileWriter(file1,true);
			BufferedWriter fbw = new BufferedWriter(fstream);
	        fbw.write(str);
	        fbw.newLine();
	        fbw.close();
		} 
		catch (IOException e) 
		{				
			e.printStackTrace();
			return 1;
		}
        return 0;
	}
	
	public synchronized static void fileRead(Message m, int server_id)
	{
		System.out.println("******** READ FROM FILE *******");
		
		String file2;
		file2 = "S" + server_id + "/" + m.file_name;
		
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(file2));
			String strLine = null, tmp;
			strLine= "";
			  
			while ((tmp = br.readLine()) != null)
			{
				strLine = tmp;
			}
			
			m.ack = strLine;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

