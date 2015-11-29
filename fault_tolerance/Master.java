
public class Master {

	int port;
	int server_id;
	static int first_excep = 0;
	static int first_not = 0;
	static int count_unblock = 0;
	static volatile int failed_server = 0;

	public static void main(String[] args) {
		
		/* (1) Create Server socket and listen on the port */
		Master mt = new Master();
	    
		mt.server_id = Integer.valueOf(args[0]).intValue();
		mt.port = Integer.valueOf(args[1]).intValue();
		
		
		try
		{								
			MasterThread ts = new MasterThread(mt.port, mt.server_id);
			Thread t = new Thread(ts);
			//Thread.sleep(2000);
			t.start();			
		}		 
		catch (Exception e) 
		{					
			e.printStackTrace();
			System.exit(0);
			
		}
		
		
	}

}
