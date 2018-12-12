package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

	private final int port = 8080;
	private Selector selector,selector2;
	private ServerSocketChannel serverChannel;
	private SelectionKey serverKey, serverKey2;
	static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<SelectionKey, ClientSession>();
	
	public void run()
	{
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
			serverKey2 = serverChannel.register(selector2 = Selector.open(), SelectionKey.OP_ACCEPT);
			serverChannel.bind(new InetSocketAddress("localhost", port));			
		
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				loop(selector, serverKey);
			}, 0, 50, TimeUnit.MILLISECONDS); 
			
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				loop(selector2, serverKey2);
			}, 0, 50, TimeUnit.MILLISECONDS); 
			
			System.out.println("Server listening on: " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loop(Selector selector, SelectionKey serverKey)
	{
		try {
			selector.select();			
			
			for (SelectionKey key : selector.selectedKeys()) {
                try {
                        if (!key.isValid())
                                continue;

                        if (key == serverKey) {
                                SocketChannel acceptedChannel = serverChannel.accept();
                                if(selector == this.selector)
                                {
                                	System.out.println("Me conecto al selector 1");
                                }                                
                                else if(selector == this.selector2)
                                {
                                	System.out.println("Me conecto al selector 2");
                                }
                                else
                                {
                                	System.out.println("No se que estoy haciendo");
                                }
                                
                                if (acceptedChannel == null)
                                        continue;

                                acceptedChannel.configureBlocking(false);
                                SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                                clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));

                                System.out.println("New client " + acceptedChannel.getRemoteAddress());
                        }

                        if (key.isReadable()) {
                            if(selector == this.selector)
                            {
                            	System.out.println("Envio con selector 1");
                            }                                
                            else if(selector == this.selector2)
                            {
                            	System.out.println("Envio con selector 2");
                            }
                            else
                            {
                            	System.out.println("No se que estoy haciendo");
                            }
                        	ClientSession sesh = clientMap.get(key);

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
