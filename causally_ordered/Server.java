import java.io.*;
import java.net.*;

public class Server {

	int port;
	int server_id;
	String path;
	ServerSocket servsock = null;
	Socket consock = null;
		
	public static void main(String[] args) throws IOException{
							
		    Server serv = new Server();
		    
			serv.port = Integer.valueOf(args[0]).intValue();
			
			serv.server_id = Integer.valueOf(args[1]).intValue();
			serv.path = args[2];
			
			try
			{
				serv.servsock  = new ServerSocket(serv.port);
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
					serv.consock = serv.servsock.accept();
					System.out.println("read object received from the client");
					
					ServerThread st = new ServerThread(serv.consock, serv.server_id, serv.path);
					Thread t = new Thread(st);
					Thread.sleep(2000);
					t.start();
					
				}
				catch(IOException e)
				{
					e.printStackTrace();
					System.exit(0);
				} 
				catch (InterruptedException e) 
				{					
					e.printStackTrace();
					System.exit(0);
				}
			}			
			
	}
	
	
}


class ServerThread implements Runnable {

	Socket child;
	ObjectInputStream ois= null;
	ObjectOutputStream oos = null;
	int server_id;
	String path;
	
	
	public ServerThread(Socket consock, int serv_id, String p) {
		this.child = consock;
		this.server_id = serv_id;
		this.path = p;
		
		try 
		{
			ois = new ObjectInputStream(child.getInputStream());
			oos = new ObjectOutputStream(child.getOutputStream());
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true)
		{
			try 
			{
				Message message = (Message)(ois.readObject());
				Message serv_reply = new Message();
				
				if(message.msg_type.equals("request"))
				{
					serv_reply.message_id = message.message_id;
					serv_reply.file_id = message.file_id;
					serv_reply.release = 1;
					serv_reply.content = message.content;
					serv_reply.source_clientId = message.source_clientId;
					serv_reply.source_server = server_id;
					
					String file_name = "file" + Integer.toString(message.file_id) + ".txt";
					if(message.request.equals("READ"))
					{
						System.out.println("Message received from client: "+ message.source_clientId + "  to "+ message.request +" " +file_name);  
						
										
						BufferedReader br = new BufferedReader(new FileReader(file_name));
						
						
						String strLine = null, tmp;
						strLine= "";
						  
						while ((tmp = br.readLine()) != null)
						{
							strLine = tmp;
						}
						
						System.out.println("*************** Content Read from the File ****************");
						System.out.println(strLine);
						serv_reply.content = strLine;
						oos.writeObject(serv_reply);
						
						br.close();
						
						
					}
					else if(message.request.equals("WRITE"))
					{
						System.out.println("Message received from client: "+ message.source_clientId + "  to "+ message.request +" file" +" " +file_name);
						
						//append  line at the end of file
						FileWriter fstream = new FileWriter(file_name,true);
			            BufferedWriter fbw = new BufferedWriter(fstream);
			            fbw.write(message.content);
			            fbw.newLine();
			            fbw.close();
			            		           
			            oos.writeObject(serv_reply);
			            oos.flush();
			            
					}	
				}
				else if(message.msg_type.equals("enquiry"))
				{
					File dir = new File(path);
					String[] chld = dir.list();
					String lst = "";
					
					if(chld == null)
					{
						lst = "";
					}
					else
					{
						for(int i = 0; i < chld.length; i++)
						{
							  String fileName = chld[i];
							  lst = lst + "" + fileName+ "," ;
						}
					}
					
					serv_reply.content = lst;
					serv_reply.source_server = server_id;
					
					oos.writeObject(serv_reply);
		            oos.flush();
				}
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.exit(0);
			} 
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
				System.exit(0);
			}				
			
		}
	}
	
}