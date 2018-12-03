package servidor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;


public class SesionCliente{

	private SocketChannel channel;
	private SelectionKey selKey;

	private String nombre;
	
	public SesionCliente(SelectionKey selKey, SocketChannel channel)
	{
		try {
			this.selKey = selKey;
			this.channel = (SocketChannel) channel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	private void disconnect()
	{
		try 
		{
			Servidor.clientMap.remove(selKey);
			if(selKey != null) selKey.cancel();
			if(channel == null) return;					
			System.out.println("Cliente " + (InetSocketAddress) channel.getRemoteAddress() + " desconectado");
			channel.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void read()
	{
        ByteBuffer buf = ByteBuffer.allocate(256);
		try {
            int amount_read = -1;

            amount_read = channel.read(buf);            

            if (amount_read == -1)
                    disconnect();

            if (amount_read < 1)
                    return;
            
            String input = new String(buf.array()).trim();
            if (input.charAt(0) == '#')
            {
            	int opcode = Integer.parseInt((input.substring(1, 3)));
            	String data = input.substring(3, input.length());
            	switch (opcode)
            	{
            	case 1:
            		this.nombre = data;
            		sendMyName();
            		sendMsg(nameList());
            		break;
				default:
					System.out.println("comando desconocido");
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(final ByteBuffer buffer)
	{
        Servidor.clientMap.forEach((k,v) ->{
        	if (v != this) v.sendMsg(buffer);
        });
	}
	
	
	private void sendMyName()
	{
		ByteBuffer bb;
		String name = "#03".concat(this.nombre);
		bb = ByteBuffer.wrap(name.getBytes());
		broadcast(bb);
	}
	
	
	private ByteBuffer nameList()
	{
		String res = "#02";		
		
		for (HashMap.Entry<SelectionKey, SesionCliente> entry : Servidor.clientMap.entrySet()) {
			String nombre = entry.getValue().nombre;
			res = res.concat(nombre).concat(",");			
		}

		return ByteBuffer.wrap(res.substring(0, res.length()).getBytes());
	}

}
