package servidor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Servidor {

	private final int puerto = 8080;
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private SelectionKey serverKey;
	static HashMap<SelectionKey, SesionCliente> clientMap = new HashMap<SelectionKey, SesionCliente>();
	
	public void run()
	{
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
			serverChannel.bind(new InetSocketAddress("localhost", puerto));			
		
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				loop();
			}, 0, 500, TimeUnit.MILLISECONDS); 
			
			System.out.println("Servidor escuchando en el puerto: " + puerto);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	void loop()
	{
		try {
			selector.selectNow();			
			
			for (SelectionKey key : selector.selectedKeys()) {
                try {
                        if (!key.isValid())
                                continue;

                        if (key == serverKey) {
                                SocketChannel acceptedChannel = serverChannel.accept();

                                if (acceptedChannel == null)
                                        continue;

                                acceptedChannel.configureBlocking(false);
                                SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                                clientMap.put(readKey, new SesionCliente(readKey, acceptedChannel));

                                System.out.println("Nuevo cliente aceptado " + acceptedChannel.getRemoteAddress());
                        }

                        if (key.isReadable()) {
                        	SesionCliente sesh = clientMap.get(key);

                                if (sesh == null)
                                        continue;

                                sesh.read();
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        selector.selectedKeys().clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
