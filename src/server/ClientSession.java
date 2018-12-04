package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;


public class ClientSession{

	private SocketChannel channel;
	private SelectionKey selKey;
	private ByteBuffer buf;
	private String name;
	
	public ClientSession(SelectionKey selKey, SocketChannel channel)
	{
		try {
			this.selKey = selKey;
			this.channel = (SocketChannel) channel.configureBlocking(false);
			buf = ByteBuffer.allocate(256);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private void disconnect()
	{
		try 
		{
			Server.clientMap.remove(selKey);
			if(selKey != null) selKey.cancel();
			if(channel == null) return;					
			System.out.println("Client " + (InetSocketAddress) channel.getRemoteAddress() + " disconected");
			channel.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void read()
	{
        
		try {
            int amount_read = -1;
            
            amount_read = channel.read((ByteBuffer)buf.clear());            

            if (amount_read == -1)
                    disconnect();

            if (amount_read < 1)
                    return;
            
            String input = new String(buf.array()).trim().substring(0, amount_read);
            if (input.charAt(0) == '#')
            {
            	int opcode = Integer.parseInt((input.substring(1, 3)));
            	String data = input.substring(3, input.length());
            	switch (opcode)
            	{
            	case 1:
            		this.name = data;
            		sendMyName();
            		sendMsg(nameList());
            		break;
				default:
					System.out.println("Unknown command");
					break;
            	}
            	
            }
            else
            {            	
            	buf.flip();
                broadcast(buf);
            }

	    } catch (IOException e) {
	            disconnect();
	    }
	}
	
	private void sendMsg(ByteBuffer buffer)
	{
		try {
			channel.write(buffer);
			buffer.rewind();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(final ByteBuffer buffer)
	{
        Server.clientMap.forEach((k,v) ->{        	
        	if (v.selKey != this.selKey) v.sendMsg(buffer);
        });
	}
	
	
	private void sendMyName()
	{
		ByteBuffer bb;
		String name = "#03".concat(this.name);
		bb = ByteBuffer.wrap(name.getBytes());
		broadcast(bb);
	}	
	
	private ByteBuffer nameList()
	{
		String res = "#02";		
		
		for (HashMap.Entry<SelectionKey, ClientSession> entry : Server.clientMap.entrySet()) {
			String nombre = entry.getValue().name;
			res = res.concat(nombre).concat(",");			
		}

		return ByteBuffer.wrap(res.substring(0, res.length()).getBytes());
	}

}
