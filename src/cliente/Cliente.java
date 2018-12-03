package cliente;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import views.ClientLayoutController;

public class Cliente implements Runnable{

	private SocketChannel s;
	private String nombre;
	private ClientLayoutController controller;
	
	public Cliente(String nombre, ClientLayoutController controller)
	{
		this.nombre = nombre;
		this.controller = controller;		
	}
		
	@Override
	public void run()
	{	
		try {
			s = SocketChannel.open(new InetSocketAddress("localhost", 8080));			
			String msg = "#01" + nombre;			
			s.write(ByteBuffer.wrap(msg.getBytes()));
			
			ByteBuffer buffer = ByteBuffer.allocate(256);
			try {
				while(s.read(buffer) > 1)
				{				
		            String input = new String(buffer.array()).trim();
		            if (input.charAt(0) == '#')
		            {
		            	int opcode = Integer.parseInt((input.substring(1, 3)));
		            	String data = input.substring(3, input.length());
		            	switch (opcode)
						{
						case 1://nombre

							break;
						case 2://Pedir lista
							controller.listaUsuarios(data);
							break;
						case 3:
							controller.addNameToList(data);
							break;
						case 4://desconecta
							controller.removeNameToList(data);
							break;
						default:
							System.out.println("comando desconocido");
							break;
						}
		            }
		            else
		            {
		            	controller.escribirMsg(input);
		            }		            
		            buffer = ByteBuffer.allocate(256);		
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
