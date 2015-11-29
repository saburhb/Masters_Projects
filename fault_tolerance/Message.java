import java.io.Serializable;
import java.util.Comparator;

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
		int crashed_server_id;
		int newhead;
		int unique_id;
		
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

