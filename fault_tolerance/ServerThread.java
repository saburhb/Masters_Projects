import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


class ServerThread implements Runnable{
	
	int port;
	int server_id;
	ServerSocket servsock = null;
	Socket consock = null;
	
	ServerThread(int port_no, int serv_id)
	{
		this.port = port_no;
		this.server_id = serv_id;
	}
	
		
	public void run() {		
		try
		{
			servsock  = new ServerSocket(port);
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
				consock = servsock.accept();
				System.out.println("Connection socket is created");
				
				ServerChildThread st = new ServerChildThread(consock, server_id);
				Thread ts = new Thread(st);
				//.sleep(200);
				ts.start();						
			}
			catch(IOException e)
			{
				e.printStackTrace();
				continue; // testing
			} 
			
		}		
		
	}

	
}
