package client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import views.ClientLayoutController;

public class Cliente implements Runnable{

	private final int port = 8080;
	private final String ip = "localhost";
	private SocketChannel s;
	private String name;
	private ClientLayoutController controller;
	
	public Cliente(String name, ClientLayoutController controller)
	{
		this.name = name;
		this.controller = controller;		
	}
		
	@Override
	public void run()
	{	
		try {
			s = SocketChannel.open(new InetSocketAddress(ip, port));			
			String msg = "#01" + name;			
			s.write(ByteBuffer.wrap(msg.getBytes()));
			
			ByteBuffer buffer = ByteBuffer.allocate(256);
			int data_size;
			try {
				while((data_size = s.read((ByteBuffer)buffer.clear())) > 0)
				{				
		            String input = new String(buffer.array()).trim().substring(0, data_size);
		            if (input.charAt(0) == '#')
		            {
		            	int opcode = Integer.parseInt((input.substring(1, 3)));
		            	String data = input.substring(3, input.length());
		            	switch (opcode)
						{
						case 1:

							break;
						case 2:
							controller.listaUsuarios(data);
							break;
						case 3:
							controller.addNameToList(data);
							break;
						case 4:
							controller.removeNameToList(data);
							break;
						default:
							System.out.println("Unknown command");
							break;
						}
		            }
		            else
		            {
		            	controller.escribirMsg(input);
		            }       
		            
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void enviarMsg(String msg)
	{
		try {
			s.write(ByteBuffer.wrap(msg.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
